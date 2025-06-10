package org.example.car_management_system.config.exception;

public class ManufactureNotFoundException extends RuntimeException {
    public ManufactureNotFoundException(String message) {
        super(message);
    }
}
