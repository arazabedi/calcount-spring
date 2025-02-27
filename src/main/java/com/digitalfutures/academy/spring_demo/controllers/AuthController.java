package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.dto.LoginRequest;
import com.digitalfutures.academy.spring_demo.dto.RegistrationRequest;
import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.service.PasswordService;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AuthController {

    // Use final + @AllArgsConstructor instead of discouraged @Autowire to auto-handle injection in Spring
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Username is already taken"));

        }

        // Check if email already exists
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is already in use"));
        }

        // Register the user
        User registeredUser = userService.registerUser(
                registrationRequest.getUsername(),
                registrationRequest.getFullName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    // Login a user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Find user by username
            User user = userRepository.findByUsername(loginRequest.getUsername());

            // Check if user exists
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password"));
            }

            // Verify password
            boolean isPasswordValid = passwordService.verifyPassword(
                    loginRequest.getPassword(),
                    user.getHashedPassword()
            );

            if (!isPasswordValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password"));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());

            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred during login",
                            "error", e.getMessage()));
        }
    }
}