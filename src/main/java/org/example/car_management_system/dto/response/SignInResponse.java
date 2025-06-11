package org.example.car_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    private UUID userId;
    private String username;
    private String token;
    private String refreshToken;
}
