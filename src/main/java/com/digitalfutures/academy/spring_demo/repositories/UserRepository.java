package com.digitalfutures.academy.spring_demo.repositories;

import com.digitalfutures.academy.spring_demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository <User, String> {

}
