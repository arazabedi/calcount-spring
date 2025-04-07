package com.digitalfutures.academy.spring_demo.dto.response;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Getter
@Data // Needed to override the toString/equals/hashCode methods for testing expected responses
public class UserDetailsResponse {
    private String id;
    private String username;
    @JsonProperty("full_name")
    private FullName fullName;
    private String email;

    // Convert user object to registration response (constructor)
    public UserDetailsResponse(User user) {
        this.id = user.get_id();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
    }
}