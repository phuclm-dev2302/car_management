package org.example.car_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.car_management_system.dto.request.OtpRequest;
import org.example.car_management_system.dto.request.SignInRequest;
import org.example.car_management_system.dto.request.SignUpRequest;
import org.example.car_management_system.dto.response.SignInResponse;
import org.example.car_management_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }


    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest signInForm, HttpServletRequest request) {
        SignInResponse response = authService.login(signInForm, request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<String> confirmActiveAccount(@RequestBody OtpRequest request, @PathVariable("id") UUID id){
        return ResponseEntity.ok(authService.confirmActiveAccount(id, request));
    }
}
