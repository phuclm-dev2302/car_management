package org.example.car_management_system.service.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.car_management_system.common_event.OtpEmailMessage;
import org.example.car_management_system.dto.request.OtpRequest;
import org.example.car_management_system.dto.request.SignInRequest;
import org.example.car_management_system.dto.request.SignUpRequest;
import org.example.car_management_system.dto.response.SignInResponse;
import org.example.car_management_system.enums.UserStatusEnum;
import org.example.car_management_system.exception.LoginBlockedException;
import org.example.car_management_system.exception.UserExistException;
import org.example.car_management_system.jwtconfig.JwtProvider;
import org.example.car_management_system.model.Role;
import org.example.car_management_system.model.User;
import org.example.car_management_system.rabbitmq.RabbitMQProducer;
import org.example.car_management_system.repository.RoleRepository;
import org.example.car_management_system.repository.UserRepository;
import org.example.car_management_system.service.AuthService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RabbitMQProducer rabbitMQProducer;
    private final RedisTemplate<String,String> redisTemplate;
    private final RoleRepository roleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RateLimiterService rateLimiterService;

    @Override
    public SignInResponse login(SignInRequest form, HttpServletRequest request) {
        String username = form.getUsername();
        String ip = request.getRemoteAddr();
        String key = "ip:" + ip;


        if(rateLimiterService.isBlocked(username)){
            log.warn("User bi chan do spam dang nhap that bai", username);
            throw new LoginBlockedException("Too many login attempts. Please try again later.");
        }

        User user = userRepository.findByUsername(form.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + form.getUsername()));

        if (!user.getStatus().equals(UserStatusEnum.ACTIVE)) {
            throw new AccountNotActiveException("Account is not active");
        }
        rabbitTemplate.convertAndSend("fanout-exchange", "", "Hello các listener!");

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword())
            );
        } catch (AuthenticationException e) {
            // luu(record) cache so lan that bai
            rateLimiterService.recordFailedAttempt(username);

            log.error("Authentication failed for username: {} with exception: {}", username, e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtProvider.generateToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        log.info("User {} logged in successfully with ", user.getUsername());

        return new SignInResponse(
                user.getUserId(),
                user.getUsername(),
                accessToken,
                refreshToken
        );
    }
    @Override
    @Transactional
    public String register(SignUpRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserExistException("Username already in use");
        }

        Role role = roleRepository.findByName("ROLE_USER").orElse(null);
        if (role == null) {
            Role role1 = new Role();
            role1.setName("ROLE_USER");
            roleRepository.save(role1);
            role = role1;
        }

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        log.info("🔑 Generated OTP: {}", otpCode);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(role)
                .status(UserStatusEnum.NONACTIVE)
                .build();
        userRepository.save(user);

        // Lưu OTP vào Redis với TTL 5 phút
        String otpKey = "otp:" + otpCode;
        redisTemplate.opsForValue().set(otpKey, otpCode, Duration.ofMinutes(5));
        log.info("💾 Saved OTP to Redis with key: {}", otpKey);

        // Lưu email tương ứng với OTP vào Redis
        String emailKey = "email:" + otpCode;
        redisTemplate.opsForValue().set(emailKey, request.getEmail(), Duration.ofMinutes(5));
        log.info("📧 Saved email to Redis with key: {}, email: {}", emailKey, request.getEmail());

        // Verify ngay lập tức xem có lưu được không
        String verifyOtp = redisTemplate.opsForValue().get(otpKey);
        String verifyEmail = redisTemplate.opsForValue().get(emailKey);
        log.info("✅ Verification - OTP: {}, Email: {}", verifyOtp, verifyEmail);

        // Gửi message qua RabbitMQ để gửi email
        OtpEmailMessage otpEmailMessage = new OtpEmailMessage(request.getEmail(), otpCode);
        rabbitMQProducer.sendMessageMail(otpEmailMessage);
        log.debug("Sent message to RabbitMQ [exchange: send-mail-active, routing-key: send-mail] for user [{}]", user.getUsername());

        return "Register user successfully with id: " + user.getUserId() + " - OTP: " + otpCode;
    }

    @Override
    public String confirmActiveAccount(UUID id, OtpRequest request) {
        String otpKey = "otp:" + request.getOtp();
        String emailKey = "email:" + request.getOtp();

        log.info(" OTP key: {}, Email key: {}", otpKey, emailKey);

        String otpCache = redisTemplate.opsForValue().get(otpKey);
        String emailCache = redisTemplate.opsForValue().get(emailKey);

        log.info("Cache redis - OTP: {}, Email: {}", otpCache, emailCache);

        if (otpCache == null || !otpCache.equals(request.getOtp())){
            log.error("OTP validation failed - Cache: {}, Provided: {}", otpCache, request.getOtp());
            throw new RuntimeException("Invalid or expired OTP code");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id:"+id));

        String userEmail = user.getEmail();
        log.info("👤 User email from DB: {}", userEmail);

        if (emailCache == null || !userEmail.equals(emailCache)){
            log.error(" Email validation failed - Cache: {}, User: {}", emailCache, userEmail);
            throw new RuntimeException("Email does not match with user");
        }

        // Xóa cache trước khi cập nhật user
        redisTemplate.delete(otpKey);
        redisTemplate.delete(emailKey);
        log.info("Deleted cache keys");

        user.setStatus(UserStatusEnum.ACTIVE);
        userRepository.save(user);
        log.info("User activated successfully");

        return "User active success !";
    }


}
