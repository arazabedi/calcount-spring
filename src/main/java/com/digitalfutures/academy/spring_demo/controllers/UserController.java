package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("api/user/weight-log")
    public ResponseEntity<?> getWeightLog() {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get weight logs for user
        List<WeightLogEntry> weightLogs = userService.getWeightLogsByUsername(currentUser.getUsername());

        // Return logs
        return ResponseEntity.ok(Map.of("weightLogs", weightLogs));
    }
}