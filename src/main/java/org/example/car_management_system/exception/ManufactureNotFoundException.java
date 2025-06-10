package org.example.car_management_system.exception;

public class ManufactureNotFoundException extends RuntimeException {
    public ManufactureNotFoundException(String message) {
        super(message);
    }
}
