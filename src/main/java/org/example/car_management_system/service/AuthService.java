package org.example.car_management_system.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.car_management_system.dto.request.OtpRequest;
import org.example.car_management_system.dto.request.SignInRequest;
import org.example.car_management_system.dto.request.SignUpRequest;
import org.example.car_management_system.dto.response.SignInResponse;

import javax.management.relation.RoleNotFoundException;
import java.util.UUID;

public interface AuthService {
    SignInResponse login(SignInRequest form, HttpServletRequest request);

    String register(SignUpRequest request);

    String confirmActiveAccount(UUID id, OtpRequest request);

}
