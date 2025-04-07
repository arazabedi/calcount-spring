package com.digitalfutures.academy.spring_demo.model;

import com.digitalfutures.academy.spring_demo.service.PasswordService;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Document("users")
@NoArgsConstructor // For testing purposes
public class User {

    @Setter
    @Id
    @JsonProperty("id")
    private String _id;

    @JsonProperty("username")
    @NotEmpty(message = "User must have a username")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    @Indexed(unique = true)
    @Getter
    @Setter
    private String username;

    @JsonProperty("full_name")
    @NotNull(message = "User must have a full name")
    @Valid
    @Getter
    @Setter
    private FullName fullName;

    @JsonProperty("email")
    @NotEmpty(message = "User must have an email")
    @Email
    @Size(max = 50, message = "Email must be less than 50 characters")
    @Indexed(unique = true)
    private String email;

    @Setter
    @JsonIgnore // Do not expose the hashed password in any JSON serialization
    private String hashedPassword;

    // For storing user IDs that represent sent friend requests
    @Setter
    @JsonProperty("sent_requests")
    private List<String> sentRequests = new ArrayList<>();

    // For storing user IDs that represent received friend requests
    @Setter
    @JsonProperty("friend_requests")
    private List<String> friendRequests = new ArrayList<>();

    // For storing user IDs of confirmed friends
    @Setter
    @JsonProperty("friends")
    private List<String> friends = new ArrayList<>();

    // For storing weight log entries
    @Setter
    @JsonProperty("weight_log")
    @Valid
    private List<WeightLogEntry> weightLog = new ArrayList<>();

    public User(String username, FullName fullName, String email) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

    // Enforces password hashing at the model level - replaces setter for hashedPassword
    public void setHashedPassword(String rawPassword, PasswordService passwordService) {
        this.hashedPassword = passwordService.hashPassword(rawPassword);
    }
}