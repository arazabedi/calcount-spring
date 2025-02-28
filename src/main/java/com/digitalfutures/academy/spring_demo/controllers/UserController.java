package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("api/user/weight-log")
    public ResponseEntity<?> getWeightLog() {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get weight logs for user
        List<WeightLogEntry> weightLogs = userService.getWeightLogsByUsername(currentUser.getUsername());

        // Return logs
        return ResponseEntity.ok(Map.of("weightLogs", weightLogs));
    }

    @PostMapping("api/user/weight-log")
    public ResponseEntity<?> postWeightLogEntry(@RequestBody WeightLogEntry weightLogEntry) {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Add weight log entry
        userService.addWeightLogEntry(currentUser.getUsername(), weightLogEntry);

        // Return success
        return ResponseEntity.ok(Map.of("message", "Weight log entry added successfully"));
    }

    @GetMapping(value = "/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/api/users/search")
    public User searchForUserByUsername(@RequestParam String username) {
        return userService.findByUsername(username);
    }
}