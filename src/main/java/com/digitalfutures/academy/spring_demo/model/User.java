package com.digitalfutures.academy.spring_demo.model;

import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Document("users")
public class User {

    @Getter
    @Id
    @JsonProperty("id")
    private String _id;

    @Getter
    @JsonProperty("username")
    @NotEmpty(message = "User must have a username")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    @Indexed(unique = true)
    private String username;

    @Getter
    @JsonProperty("full_name")
    @NotEmpty(message = "User must have a full name")
    @Valid
    private FullName fullName;

    @Getter
    @JsonProperty("email")
    @NotEmpty(message = "User must have an email")
    @Email
    @Size(max = 50, message = "Email must be less than 50 characters")
    @Indexed(unique = true)
    private String email;

    @Getter
    @Setter
    @JsonIgnore // Do not expose the hashed password in any JSON serialization
    private String hashedPassword;

    // For storing user IDs that represent sent friend requests
    @Getter
    @Setter
    @JsonProperty("sent_requests")
    private List<String> sentRequests = new ArrayList<>();

    // For storing user IDs that represent received friend requests
    @Getter
    @Setter
    @JsonProperty("friend_requests")
    private List<String> friendRequests = new ArrayList<>();

    // For storing user IDs of confirmed friends
    @Getter
    @Setter
    @JsonProperty("friends")
    private List<String> friends = new ArrayList<>();

    // For storing weight log entries
    @Getter
    @Setter
    @JsonProperty("weight_log")
    @Valid
    private List<WeightLogEntry> weightLog = new ArrayList<>();

    public User(String username, FullName fullName, String email) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }
}