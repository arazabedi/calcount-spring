package com.digitalfutures.academy.spring_demo.service;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.shared.FriendData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendService {

    private final UserRepository userRepository;

    // Returns the user data (see getUserData) of all friends of the user corresponding the passed id
    public List<FriendData> getAllFriendWeightLogs(String userId) {
        // Retrieve the current user
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Retrieve a list of friend ids
        List<String> friendIds = user.getFriends();

        // Create a list to store friend data in the desired format
        List<FriendData> friendData = new ArrayList<>();

        // Loop through each friend and create a map (corresponding to a JSON object) with the friend's data
        for (String friendId : friendIds) {
            FriendData friendInfo = getIndividualFriendData(friendId);
            if (friendInfo != null) {
                friendData.add(friendInfo);
            }
        }

        return friendData;
    }

    // Helper for getAllFriendWeightLogs to retrieve the friend's data
    private FriendData getIndividualFriendData(String id) {
        // Retrieve the user corresponding to the passed id
        User friend = userRepository.findById(id).orElse(null);

        if (friend == null) {
            return null;
        }

        // Create a FriendData object with the friend's data
        return new FriendData(
                friend.get_id(),
                friend.getUsername(),
                friend.getFullName(),
                friend.getWeightLog()
        );
    }

    // Sends a friend request by adding the receiver's id to the current user's sentRequests list and vice versa
    @Transactional // Ensures both users are saved or neither are saved - rollback if an exception occurs
    public void sendFriendRequest(String userId, String receiverId) {
        // Check if the user is trying to send a friend request to themselves
        if (userId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to self");
        }

        // Retrieve the current user - orElse(null) used to avoid using the Optional<User> type that findById returns
        User user = userRepository.findById(userId).orElse(null);
        // Retrieve the receiver of the friend request
        User receiver = userRepository.findById(receiverId).orElse(null);

        // Check if either user or receiver is null OR both are null
        if (user == null && receiver == null) {
            throw new RuntimeException("Neither user found");
        } else if (user == null) {
            throw new RuntimeException("User not found");
        } else if (receiver == null) {
            throw new RuntimeException("Receiver not found");
        }

        // Validate if a friend request can be sent
        validateFriendRequest(user, receiver);

        // Add the receiver's id to the current user's sentRequests list
        user.getSentRequests().add(String.valueOf(receiver.get_id()));
        // Add the current user's id to the receiver's friendRequests list
        receiver.getFriendRequests().add(String.valueOf(user.get_id()));

        // Save to user repository
        userRepository.save(user);
        userRepository.save(receiver);
    }

    // Helper for sendFriendRequest to validate if a friend request can be sent
    private void validateFriendRequest(User user, User receiver) {
        if (user.getFriends().contains(receiver.get_id()) || receiver.getFriends().contains(user.get_id())) {
            throw new RuntimeException("Users are already friends");
        }
        if (user.getSentRequests().contains(receiver.get_id()) || receiver.getSentRequests().contains(user.get_id())) {
            throw new RuntimeException("A friend request has already been sent");
        }
        if (user.getFriendRequests().contains(receiver.get_id()) || receiver.getFriendRequests().contains(user.get_id())) {
            throw new RuntimeException("A friend request is already pending from the other user");
        }
    }

    // Accepts a friend request by adding the requester's id to the current user's friends list and vice versa
    @Transactional
    public void acceptFriendRequest(String userId, String requesterId){
        // Retrieve the current user
        User user = userRepository.findById(userId).orElse(null);
        // Retrieve the requester of the friend request
        User requester = userRepository.findById(requesterId).orElse(null);

        // Check if either user or requester is null OR both are null
        if (user == null && requester == null) {
            throw new RuntimeException("Neither user found");
        } else if (user == null) {
            throw new RuntimeException("User not found");
        } else if (requester == null) {
            throw new RuntimeException("Requester not found");
        }

        // Check if the users are already friends
        if (user.getFriends().contains(requesterId) || requester.getFriends().contains(userId)) {
            throw new RuntimeException("Users are already friends");
        }

        // Check if the requester has sent a friend request
        if (!user.getFriendRequests().contains(requesterId)) {
            throw new RuntimeException("No friend request found");
        }

        // Add the requester's id to the current user's friends list
        user.getFriends().add(String.valueOf(requester.get_id()));
        // Add the current user's id to the requester's friends list
        requester.getFriends().add(String.valueOf(user.get_id()));
        // Remove the request from the user's friendRequest list
        user.getFriendRequests().remove(String.valueOf(requester.get_id()));
        // Remove the request from the requester's sentRequests list
        requester.getSentRequests().remove(String.valueOf(user.get_id()));

        // Save to user repository
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
    @Transactional // Ensures both users are saved or neither are saved - rollback if an exception occurs
    public void removeFriend(String userId, String friendId) {
        // Retrieve the current user and the friend to be removed
        User user = userRepository.findById(userId).orElse(null);
        User friend = userRepository.findById(friendId).orElse(null);

        // Check if either user or requester is null OR both are null
        if (user == null && friend == null) {
            throw new RuntimeException("Neither user found");
        } else if (user == null) {
            throw new RuntimeException("User not found");
        } else if (friend == null) {
            throw new RuntimeException("Users are not friends");
        }

        // Check if users are friends
        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(userId)) {
            throw new RuntimeException("Users are not friends");
        }

        // Remove the friend's id from both users' friends lists
        user.getFriends().remove(String.valueOf(friend.get_id()));
        friend.getFriends().remove(String.valueOf(user.get_id()));

        // Save to user repository
        userRepository.save(user);
        userRepository.save(friend);
    }
}
