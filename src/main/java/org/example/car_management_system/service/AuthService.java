package org.example.car_management_system.service;

import org.example.car_management_system.dto.request.SignInRequest;
import org.example.car_management_system.dto.request.SignUpRequest;
import org.example.car_management_system.dto.response.SignInResponse;

import javax.management.relation.RoleNotFoundException;

public interface AuthService {
    SignInResponse login(SignInRequest request);

    String register(SignUpRequest request);

}
