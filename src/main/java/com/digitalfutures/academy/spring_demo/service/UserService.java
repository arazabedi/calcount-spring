package com.digitalfutures.academy.spring_demo.service;

import com.digitalfutures.academy.spring_demo.dto.response.RegistrationResponse;
import com.digitalfutures.academy.spring_demo.exceptions.UserNotFoundException;
import com.digitalfutures.academy.spring_demo.exceptions.WeightLogEntryEmptyException;
import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    // Use final fields meaning they must be provided a object creation - Spring handles the rest
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public RegistrationResponse registerUser(String username, FullName fullName, String email, String rawPassword) {
        // Avoids unnecessary database calls and highly testable - despite @Indexed(unique = true) in the model
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        // Create user without password
        User user = new User(username, fullName, email);

        // Hash the password and set it to the user
        String hashedPassword = passwordService.hashPassword(rawPassword);
        user.setHashedPassword(hashedPassword);

        // Save the user to the database
        userRepository.save(user);

        // Return the JSON representation of the user
        return new RegistrationResponse(user);
    }

    public boolean authenticateUser(String username, String rawPassword) {
        // Retrieve the user from the database
        User user = userRepository.findByUsername(username);

        // Check if the user was found
        if (user == null) {
            // Don't throw an exception here - that would be a security risk
            // A hacker shouldn't be able to determine if a user exists by sending a request with a valid username
            // Instead, return false to indicate authentication failure
            return false;
        }

        // Verify the password using the password service
        return passwordService.verifyPassword(rawPassword, user.getHashedPassword());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> searchByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }

    // Get weight log entries for a user
    public List<WeightLogEntry> getWeightLog(String id) {
        // Retrieve the user from the database
        Optional<User> user = userRepository.findById(id);

        // Check if the user was found
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        // Return the user's weight log
        return user.get().getWeightLog();
    }

    // Add a weight log entry to a user's weight log
    public void addWeightLogEntry(String id, WeightLogEntry entry) {
        // Retrieve the user from the database
        Optional<User> user = userRepository.findById(id);

        if (entry.isEmpty()) {
            throw new WeightLogEntryEmptyException("Weight log entry is empty");
        }

        // Check if the user was found
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        // Add the weight log entry to the user's weight log
        user.get().getWeightLog().add(entry);

        // Save the updated user to the database
        userRepository.save(user.get());
    }

    public List<String> getAllUsernames() {
        // Retrieve all users
        List<User> users = userRepository.findAll();

        // Create a list to store usernames
        List<String> usernames = new ArrayList<>();

        // Loop through each user and add their username to the list
        for (User user : users) {
            usernames.add(user.getUsername());
        }

        // Return the list of usernames
        return usernames;
    }

    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}