package com.digitalfutures.academy.spring_demo.helpers;

import com.digitalfutures.academy.spring_demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class JsonFileReader {
    private static ArrayList<User> users;

    public static List<User> filetoObjectList() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(
                    JsonFileReader.class.getResourceAsStream("/data.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, User.class)
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}