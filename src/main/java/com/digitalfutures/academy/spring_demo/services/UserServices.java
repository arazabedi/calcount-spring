package com.digitalfutures.academy.spring_demo.services;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<WeightLogEntry> getWeightLogsByUsername(String _id) {
        User user = userRepository.findById(_id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + _id));
        return user.getWeightLog();
    }


//    public User addUser(User user) {
//        return userRepository.save(user);
//    }
//
//    public User updateTodo(String _id, User user) {
//        if(!userRepository.existsById(_id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That todo could not be found");
//        return userRepository.save(user);
//    }
}
