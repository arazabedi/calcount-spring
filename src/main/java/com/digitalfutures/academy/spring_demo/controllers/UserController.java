package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.dto.response.UserDetailsResponse;
import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@AllArgsConstructor
@CrossOrigin(origins = "*")
// Exceptions are handled by the GlobalExceptionHandler class
public class UserController{

    private final UserService userService;

    @GetMapping("api/user/weight-log")
    public ResponseEntity<?> getWeightLog() {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get weight logs for user
        List<WeightLogEntry> weightLog = userService.getWeightLog(currentUser.get_id());

        // Return logs
        return ResponseEntity.ok(weightLog);
    }

    @PostMapping("api/user/weight-log")
    public ResponseEntity<?> postWeightLogEntry(@RequestBody WeightLogEntry weightLogEntry) {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Add weight log entry
        userService.addWeightLogEntry(currentUser.get_id(), weightLogEntry);

        // Return success
        return ResponseEntity.ok(Map.of("message", "Weight log entry added successfully"));
    }

    // This endpoint is for development purposes only - it returns all users in the database
    @GetMapping(value = "/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/api/users/search")
    public List<User> searchForUserByUsername(@RequestParam String username) {
        return userService.searchByUsername(username);
    }

    @GetMapping(value = "/api/users/{username}")
    public UserDetailsResponse getUserDetails(@PathVariable String username) {
        return new UserDetailsResponse(userService.findByUsername(username));
    }

    @GetMapping(value = "/api/users/id/{id}")
    public UserDetailsResponse getUserById(@PathVariable String id) {
        return new UserDetailsResponse(userService.findById(id));
    }

    @GetMapping(value = "/api/users/usernames")
    public List<String> getAllUsernames() {
        return userService.getAllUsernames();
    }
}