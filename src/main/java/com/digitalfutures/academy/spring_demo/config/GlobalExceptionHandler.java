package com.digitalfutures.academy.spring_demo.config;

import com.digitalfutures.academy.spring_demo.exceptions.UserNotFoundException;
import com.digitalfutures.academy.spring_demo.exceptions.WeightLogEntryEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// @RestControllerAdvice is a specialised @Component annotation marking the class as a Spring bean
// Allows this class to handle all exceptions thrown by @RequestMapping methods (e.g. @GetMapping, @PostMapping, etc.)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles UserNotFoundException - returns a 404 status code with a message
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    // Handles WeightLogEntryEmptyException - returns a 400 status code with a message
    @ExceptionHandler(WeightLogEntryEmptyException.class)
    public ResponseEntity<String> handleWeightLogEntryEmptyException(WeightLogEntryEmptyException ex) {
        // You can customize this message or use a DTO to return more details
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handles all other exceptions - returns a 500 status code with a message
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred", "error", ex.getMessage()));
    }
}