package com.digitalfutures.academy.spring_demo.model;

import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document("users")
public class User {
    @Id
    @JsonProperty("id")
    private String _id;

    @JsonProperty("username")
    @NotEmpty(message="User must have a username")
    private String username;

    @JsonProperty("full_name")
    @NotEmpty(message="User must have a full name")
    private Map<String, String> fullName = new HashMap<>();

    @JsonProperty("email")
    @NotEmpty(message="User must have an email")
    private String email;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message="User must have a password")
    private String password;

    // For storing user IDs that represent sent friend requests
    @JsonProperty("sent_requests")
    private List<String> sentRequests = new ArrayList<>();

    // For storing user IDs that represent received friend requests
    @JsonProperty("friend_requests")
    private List<String> friendRequests = new ArrayList<>();

    // For storing user IDs of confirmed friends
    @JsonProperty("friends")
    private List<String> friends = new ArrayList<>();

    // For storing weight log entries
    @JsonProperty("weight_log")
    private List<WeightLogEntry> weightLog = new ArrayList<>();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public @NotEmpty(message = "User must have a username") String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty(message = "User must have a username") String username) {
        this.username = username;
    }

    public @NotEmpty(message = "User must have a full name") Map<String, String> getFullName() {
        return fullName;
    }

    public void setFullName(@NotEmpty(message = "User must have a full name") Map<String, String> fullName) {
        this.fullName = fullName;
    }

    public @NotEmpty(message = "User must have an email") String getEmail() {
        return email;
    }

    public void setEmail(@NotEmpty(message = "User must have an email") String email) {
        this.email = email;
    }

    public @NotEmpty(message = "User must have a password") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "User must have a password") String password) {
        this.password = password;
    }

    public List<String> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(List<String> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public List<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<WeightLogEntry> getWeightLog() {
        return weightLog;
    }

    public void setWeightLog(List<WeightLogEntry> weightLog) {
        this.weightLog = weightLog;
    }
}
