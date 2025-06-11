package org.example.car_management_system.exception;

public class LoginBlockedException extends RuntimeException {
    public LoginBlockedException(String message) {
        super(message);
    }
}
