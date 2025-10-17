package com.example.config;

import com.example.exception.AuthenticationException;
import com.example.exception.ResourceConflictException;
import com.example.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));
        return ResponseEntity.badRequest().body(errors);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errorResponse = Map.of(
            "message", ex.getMessage(),
            "status", String.valueOf(HttpStatus.NOT_FOUND.value())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Map<String, String>> handleResourceConflictException(ResourceConflictException ex) {
        Map<String, String> errorResponse = Map.of(
            "message", ex.getMessage(),
            "status", String.valueOf(HttpStatus.CONFLICT.value())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> errorResponse = Map.of(
            "message", ex.getMessage(),
            "status", String.valueOf(HttpStatus.UNAUTHORIZED.value())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        String message = "Internal server error occurred."; 
        Map<String, String> errorResponse = Map.of(
            "message", message,
            "status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
