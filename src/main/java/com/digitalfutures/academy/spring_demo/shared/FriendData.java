package com.digitalfutures.academy.spring_demo.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// @Data needed for equals() and hashCode() to work properly for test assertions
@Data
public class FriendData {
    @JsonProperty("friend_id")
    private String friendId;
    @JsonProperty("friend_username")
    private String friendUsername;
    @JsonProperty("friend_full_name")
    private FullName friendName;
    @JsonProperty("weight_log")
    private List<WeightLogEntry> weightLog;
}