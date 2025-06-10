package org.example.car_management_system.common_event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpEmailMessage implements Serializable {
    private String email;
    private String otp;
}