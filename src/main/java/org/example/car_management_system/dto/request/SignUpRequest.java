package org.example.car_management_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "username must be not blank!")
    private String username;

    @NotBlank(message = "password must be not blank!")
    private String password;

    @Email(message = "Email must be ...!")
    private String email;

    @NotBlank(message = "confirmPassword must be not blank!")
    private String confirmPassword;
}
