package org.example.car_management_system.config.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.car_management_system.dto.response.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ResponseError<Object>> handleCarNotFound(CarNotFoundException ex) {
        ResponseError<Object> errorResponse = new ResponseError<>(404, ex.getMessage());
        log.error("Cars was not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(ManufactureNotFoundException.class)
    public ResponseEntity<ResponseError<Object>> handleManufactureNotFound(ManufactureNotFoundException ex){
        log.error("Manufacture was not found {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseError<>(404, ex.getMessage()));
    }

    @ExceptionHandler(DuplicateManufactureException.class)
    public ResponseEntity<ResponseError<Object>> handleManufactureNotFound(DuplicateManufactureException ex){
        log.error("Manufacture with same name and country already exists {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseError<>(404, ex.getMessage()));
    }


}
