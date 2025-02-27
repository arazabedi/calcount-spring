package com.digitalfutures.academy.spring_demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Handles hashing and verification of passwords using BCrypt
@Service // Tells Spring to manage the dependency injection
public class PasswordService {
    private final PasswordEncoder passwordEncoder; // Dependency injected by Spring

    /**
     * Why use dependency injection - for Araz's future reference?
     * - Loose Coupling: Encoder can be swapped with another implementation easily.
     * - Flexibility: When changing the encoder we don't need to change this class.
     * - Testability: Mocking is easier for unit tests.
     * - Spring Lifecycle: Spring automatically handles the injection in a memory-efficient way.
     */

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
