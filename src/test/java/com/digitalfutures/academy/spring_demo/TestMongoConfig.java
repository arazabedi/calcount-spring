package com.digitalfutures.academy.spring_demo;

import com.digitalfutures.academy.spring_demo.model.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Configuration
public class TestMongoConfig {
    private static final String collectionName = "todos";

    @Bean
    public static MongoTemplate mongoTemplate() {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/todos_test"));
    }

    public static void clearCollection() {
        System.out.println("Deleting existing todos");
        mongoTemplate().remove(new Query(), collectionName);
    }

    public static void repopulateCollection(List<User> users) {
        System.out.println("Repopulating todos");
        mongoTemplate().insert(users, collectionName);
    }
}
