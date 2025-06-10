package org.example.car_management_system.service;

import org.example.car_management_system.common_event.OtpEmailMessage;

import java.util.Map;
import java.util.UUID;

public interface MailService {
    void sendOtpEmail(OtpEmailMessage message);
}
