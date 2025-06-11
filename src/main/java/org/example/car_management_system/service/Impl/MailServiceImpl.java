package org.example.car_management_system.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.car_management_system.common_event.OtpEmailMessage;
import org.example.car_management_system.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    @Override
    @RabbitListener(queues = "mail-queue")
    public void sendOtpEmail(OtpEmailMessage message) {
        log.info("Nhận OTP: " + message.getOtp() + " cho email: " + message.getEmail());
        String otpCode = message.getOtp();
        String email = message.getEmail();

        String key = "otp:" + otpCode;
        String otp = redisTemplate.opsForValue().get(key);
        if (otp == null) {
            throw new RuntimeException("OTP expired or not found");
        }

        // Gửi email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Your OTP Code");
        mailMessage.setText("Your OTP code is: " + otp + "\nThis code is valid for 5 minutes.");

        log.info("Send mail successfully with message :" + message);
        mailSender.send(mailMessage);
    }
}

