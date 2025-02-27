package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import com.digitalfutures.academy.spring_demo.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class UserController {

    private final UserServices userServices;

    @Autowired
    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping(value = "/api/users")
    public List<User> getAllTodos() {
        return userServices.getAllUsers();
    }

    @GetMapping("api/user/weight-log")
    public List<WeightLogEntry> getWeightLog() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<WeightLogEntry> weightLogs = userServices.getWeightLogsByUsername(username);
        return ResponseEntity.ok(new WeightLogResponse(weightLogs));
    }

//    @PostMapping(value = "/todos")
//    @ResponseStatus(HttpStatus.CREATED)
//    public User addTodo(@Valid @RequestBody User user) {
//        return userServices.addTodo(user);
//    }
//
//    @PutMapping(value="/todos/{_id}")
//    public User updateTodo(@PathVariable String _id, @Valid @RequestBody User user) {
//        return userServices.updateTodo(_id, user);
//    }
}