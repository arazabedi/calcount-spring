package com.digitalfutures.academy.spring_demo.controllers;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.service.FriendService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@AllArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("api/friends/weight-logs")
    public List<Map<String, Object>> getAllFriendWeightLogs() {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Return a JSON object containing the weight logs of all friends (plus their id, username, and full name)
        return friendService.getAllFriendWeightLogs(currentUser.get_id());
    }

    @PostMapping("/api/friends/requests/{_id}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String _id) {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Send friend request
        friendService.sendFriendRequest(currentUser.get_id(), _id);

        // Return success
        return ResponseEntity.ok(Map.of("message", "Friend request sent successfully"));
    }

    @PutMapping("/api/friends/requests")
    public ResponseEntity<?> acceptFriendRequest(@RequestParam String _id){
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Send friend request
        friendService.acceptFriendRequest(currentUser.get_id(), _id);

        // Return success
        return ResponseEntity.ok(Map.of("message", "Friend request accepted successfully"));
    }

    @GetMapping("api/friends/requests/received")
    public List<String> getReceivedFriendRequests() {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Return a list of user ids representing sent friend requests
        return currentUser.getFriendRequests();
    }

    @GetMapping("api/friends/requests/sent")
    public List<String> getSentFriendRequests() {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Return a list of user ids representing sent friend requests
        return currentUser.getSentRequests();
    }

    @DeleteMapping("api/friends/{id}")
    public ResponseEntity<?> removeFriend(@PathVariable String id) {
        // Get authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Remove friend from both friends lists
        friendService.removeFriend(currentUser.get_id(), id);

        // Return success
        return ResponseEntity.ok(Map.of("message", "Friend removed successfully"));
    }
}
