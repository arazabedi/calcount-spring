package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.dto.request.LoginRequest;
import com.digitalfutures.academy.spring_demo.dto.request.RegistrationRequest;
import com.digitalfutures.academy.spring_demo.dto.request.VerificationRequest;
import com.digitalfutures.academy.spring_demo.dto.response.LoginResponse;
import com.digitalfutures.academy.spring_demo.dto.response.RegistrationResponse;
import com.digitalfutures.academy.spring_demo.dto.response.VerificationResponse;
import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.service.PasswordService;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*") // Allows CORS requests from any origin
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
        RegistrationResponse registeredUser = userService.registerUser(
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

            // Create LoginResponse DTO object with the token and user info
            LoginResponse response = new LoginResponse(user, token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred during login",
                            "error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authorizationHeader, @RequestBody VerificationRequest verificationRequest) {
        // Ensure the Authorization header is present and contains the bearer prefix
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Authorization header is missing or invalid"));
        }

        // Extract username from verificationRequest
        String username = verificationRequest.getUsername();

        // Extract the token from the authorization header and remove the bearer prefix
        String token = authorizationHeader.substring(7);

        // Check if the token is valid
        if (!jwtUtil.validateToken(token, username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid token"));
        }

        // Get the user from the database
        User user = userService.findByUsername(username);

        // Check if the user exists
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not found"));
        }

        // Create the VerificationResponse DTO with the user's data
        VerificationResponse response = new VerificationResponse(user);

        // Return the user data
        return ResponseEntity.ok(Map.of("user", response));
    }
}