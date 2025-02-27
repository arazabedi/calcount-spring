package com.digitalfutures.academy.spring_demo.service;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    // Use final fields meaning they must be provided a object creation - Spring handles the rest
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(String username, FullName fullName, String email, String rawPassword) {
        // Create user without password
        User user = new User(username, fullName, email);

        // Hash the password and set it to the user
        String hashedPassword = passwordService.hashPassword(rawPassword);
        user.setHashedPassword(hashedPassword);

        // Save and return the user
        return userRepository.save(user);
    }

    public boolean authenticateUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        // Verify the password using the password service
        return passwordService.verifyPassword(rawPassword, user.getHashedPassword());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get weight log entries for a user
    public List<WeightLogEntry> getWeightLogsByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        return user.getWeightLog();
    }

    // Add a weight log entry to a user's weight log
    public User addWeightLogEntry(String username, WeightLogEntry entry) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        user.getWeightLog().add(entry);
        return userRepository.save(user);
    }
}