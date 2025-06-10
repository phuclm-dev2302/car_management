package org.example.car_management_system.service.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.car_management_system.common_event.OtpEmailMessage;
import org.example.car_management_system.dto.request.SignInRequest;
import org.example.car_management_system.dto.request.SignUpRequest;
import org.example.car_management_system.dto.response.SignInResponse;
import org.example.car_management_system.enums.UserStatusEnum;
import org.example.car_management_system.exception.RoleNotFoundException;
import org.example.car_management_system.exception.UserExistException;
import org.example.car_management_system.jwtconfig.JwtProvider;
import org.example.car_management_system.model.QRole;
import org.example.car_management_system.model.Role;
import org.example.car_management_system.model.User;
import org.example.car_management_system.rabbitmq.RabbitMQProducer;
import org.example.car_management_system.repository.UserRepository;
import org.example.car_management_system.service.AuthService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    @Override
    @Transactional
    public String register(SignUpRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserExistException("Username already in use");
        }

        QRole qRole = QRole.role;
        Role role = queryFactory.selectFrom(qRole)
                .where(qRole.name.eq("ROLE_USER"))
                .fetchOne();
        if (role == null) {
            log.error("Role not found");
            throw new RoleNotFoundException("Role not found");
        }

        // ✅ Sinh OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // ✅ Tạo user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(role)
                .status(UserStatusEnum.ACTIVE)
                .build();
        userRepository.save(user);

        // ✅ Lưu OTP vào Redis với TTL 5 phút
        String key = "otp:" + otpCode;
        redisTemplate.opsForValue().set(key, otpCode, Duration.ofMinutes(5));

        // ✅ Gửi message qua RabbitMQ để gửi email
        OtpEmailMessage otpEmailMessage = new OtpEmailMessage(request.getEmail(), otpCode);

        rabbitMQProducer.sendMessageMail(otpEmailMessage);
        log.debug("Sent message to RabbitMQ [exchange: send-mail, routing-key: send-mail-topic] for user [{}]", user.getUsername());

        return "Register user successfully with id: " + user.getUserId();
    }

    @Override
    public SignInResponse login(SignInRequest form) {
        User user = userRepository.findByUsername(form.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + form.getUsername()));

        if (!user.getStatus().equals(UserStatusEnum.ACTIVE)) {
            throw new IllegalArgumentException("Account is not active");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword())
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for email: {} with exception: {}", form.getUsername(), e.getMessage());
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

}
