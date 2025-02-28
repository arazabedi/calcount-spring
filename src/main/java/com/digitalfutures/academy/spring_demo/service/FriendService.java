package com.digitalfutures.academy.spring_demo.service;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.utils.NameFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FriendService {

    private final UserRepository userRepository;

    // Returns the user data (see getUserData) of all friends of the user corresponding the passed id
    public List<Map<String, Object>> getAllFriendWeightLogs(String userId) {
        // Retrieve the current user
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Retrieve a list of friend ids
        List<String> friendIds = user.getFriends();

        // Create a list to store friend data in the desired format
        List<Map<String, Object>> friendData = new ArrayList<>();

        // Loop through each friend and create a map (corresponding to a JSON object) with the friend's data
        for (String friendId : friendIds) {
            Map<String, Object> friendInfo = getUserData(friendId);
            if (friendInfo != null) {
                friendData.add(friendInfo);
            }
        }

        return friendData;
    }

    // Returns a map corresponding to the passed id containing the user's id, username, full name (as a map)
    private Map<String, Object> getUserData(String id) {
        // Retrieve the user corresponding to the passed id
        User friend = userRepository.findById(id).orElse(null);
        if (friend == null) {
            return null;
        }

        Map<String, Object> friendInfo = new HashMap<>();
        friendInfo.put("friend_id", friend.get_id());
        friendInfo.put("friend_username", friend.getUsername());
        friendInfo.put("friend_name", NameFormatter.createNameMap(friend.getFullName())); // Call utility method
        friendInfo.put("weight_log", friend.getWeightLog());

        return friendInfo;
    }

    // Sends a friend request by adding the receiver's id to the current user's sentRequests list and vice versa
    public void sendFriendRequest(String userId, String receiverId) {
        // Retrieve the current user - orElse(null) used to avoid using the Optional<User> type that findById returns
        User user = userRepository.findById(userId).orElse(null);
        // Retrieve the receiver of the friend request
        User friend = userRepository.findById(receiverId).orElse(null);

        if (user == null || friend == null) {
            throw new RuntimeException("User not found");
        }

        // Add the receiver's id to the current user's sentRequests list
        user.getSentRequests().add(String.valueOf(friend.get_id()));
        // Add the current user's id to the receiver's friendRequests list
        friend.getFriendRequests().add(String.valueOf(user.get_id()));
        userRepository.save(user);
        userRepository.save(friend);
    }

    // Accepts a friend request by adding the requester's id to the current user's friends list and vice versa
    public void acceptFriendRequest(String userId, String requesterId){
        // Retrieve the current user
        User user = userRepository.findById(userId).orElse(null);
        // Retrieve the requester of the friend request
        User requester = userRepository.findById(requesterId).orElse(null);

        if (user == null || requester == null) {
            throw new RuntimeException("User not found");
        }

        // Add the requester's id to the current user's friends list
        user.getFriends().add(String.valueOf(requester.get_id()));
        // Add the current user's id to the requester's friends list
        requester.getFriends().add(String.valueOf(user.get_id()));
        // Remove the request from the user's friendRequest list
        user.getFriendRequests().remove(String.valueOf(requester.get_id()));
        // Remove the request from the requester's sentRequests list
        requester.getSentRequests().remove(String.valueOf(user.get_id()));
        userRepository.save(user);
        userRepository.save(requester);
    }

    // Returns a list of user ids representing received friend requests
    public List<String> getFriendRequests(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getFriendRequests();
    }

    // Returns a list of user ids representing sent friend requests
    public List<String> getSentRequests(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getSentRequests();
    }

    // Removes the friend's id from both users' friends lists
    public void removeFriend(String userId, String friendId) {
        User user = userRepository.findById(userId).orElse(null);
        User friend = userRepository.findById(friendId).orElse(null);

        if (user == null || friend == null) {
            throw new RuntimeException("User not found");
        }

        user.getFriends().remove(String.valueOf(friend.get_id()));
        friend.getFriends().remove(String.valueOf(user.get_id()));
        userRepository.save(user);
        userRepository.save(friend);
    }
}
