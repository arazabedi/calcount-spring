package com.digitalfutures.academy.spring_demo.dto.response;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data // Needed to override the toString/equals/hashCode methods for testing expected responses
public class RegistrationResponse {
    private String id;
    private String username;
    private FullName fullName;
    private String email;
    private List<String> sentRequests;
    private List<String> friendRequests;
    private List<String> friends;
    private List<WeightLogEntry> weightLog;

    // Convert user object to registration response (constructor)
    public RegistrationResponse(User user) {
        this.id = user.get_id();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.sentRequests = user.getSentRequests();
        this.friendRequests = user.getFriendRequests();
        this.friends = user.getFriends();
        this.weightLog = user.getWeightLog();
    }
}
