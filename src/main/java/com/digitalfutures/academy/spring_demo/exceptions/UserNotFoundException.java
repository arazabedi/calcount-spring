package com.digitalfutures.academy.spring_demo.exceptions;

// Exception class for user not found scenarios
public class UserNotFoundException extends RuntimeException {
    // Constructor with message parameter
    public UserNotFoundException(String message) {
        // Calls the parent constructor with the message parameter (i.e. sets the message)
        super(message);
    }
}