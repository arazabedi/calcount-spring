package com.digitalfutures.academy.spring_demo.dto.response;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data // Needed to override the toString/equals/hashCode methods for testing expected responses
public class LoginResponse {
    private String id;
    private String username;
    private FullName fullName;
    private String email;
    private List<String> sentRequests;
    private List<String> friendRequests;
    private List<String> friends;
    private List<WeightLogEntry> weightLog;
    private String accessToken;

    // Convert user object to registration response (constructor)
    public LoginResponse(User user, String accessToken) {
        this.id = user.get_id();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.sentRequests = user.getSentRequests();
        this.friendRequests = user.getFriendRequests();
        this.friends = user.getFriends();
        this.weightLog = user.getWeightLog();
        this.accessToken = accessToken;
    }
}