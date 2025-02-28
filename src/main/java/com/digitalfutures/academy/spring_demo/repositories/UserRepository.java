package com.digitalfutures.academy.spring_demo.repositories;

import com.digitalfutures.academy.spring_demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

// Spring automatically provides CRUD operations and generates queries based on method names added to the interface
public interface UserRepository extends MongoRepository <User, String> {
    // Spring Data infers queries from the names below
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

