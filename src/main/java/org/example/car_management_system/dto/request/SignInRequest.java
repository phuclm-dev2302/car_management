package org.example.car_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank( message = "password must be not blank!")
    private String username;
    @NotBlank( message = "password must be not blank!")
    private String password;
}
