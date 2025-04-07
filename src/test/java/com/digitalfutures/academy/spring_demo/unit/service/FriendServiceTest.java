package com.digitalfutures.academy.spring_demo.unit.service;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.service.FriendService;
import com.digitalfutures.academy.spring_demo.shared.FriendData;
import com.digitalfutures.academy.spring_demo.shared.FullName;

import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Initializes and manages fields with Mockito annotations
@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    // Test data constants for improved readability and maintenance
    private static final String USER_ID = "123";
    private static final String FRIEND1_ID = "456";
    private static final String FRIEND2_ID = "789";
    private static final String MISSING_ID = "321";
    private static final String MISSING_ID2 = "654";

    // @Mock creates a dummy/mock userRepository whose methods can be stubbed
    @Mock
    private UserRepository userRepository;

    // InjectMocks instantiates FriendService and injects the mock dependencies (i.e. userRepository because it's a dependency of FriendService)
    @InjectMocks
    private FriendService friendService;

    // User objects to be tested
    private User testUser;
    private User friend1;
    private User friend2;

    @BeforeEach
    void setupBaseUsers() {
        // Create fresh user objects before each test to keep test code DRY
        testUser = createUser(USER_ID);
        friend1 = createUserWithName(FRIEND1_ID, "Nora", "Jane", "Wilde");
        friend2 = createUserWithName(FRIEND2_ID, "Kyle", "Richard", "Anderson");
    }

    // Helper method to create a basic user with ID
    private User createUser(String id) {
        User user = new User();
        user.set_id(id);
        return user;
    }

    // Helper method to create a user with ID and full name
    private User createUserWithName(String id, String firstName, String middleName, String lastName) {
        User user = createUser(id);
        user.setFullName(new FullName(firstName, middleName, lastName));
        user.setUsername(firstName.toLowerCase() + "_" + lastName.toLowerCase());
        return user;
    }

    @Nested
    @DisplayName("getAllFriendWeightLogs Tests")
    class GetAllFriendWeightLogsTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - Setup missing user scenario
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when user is missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.getAllFriendWeightLogs(MISSING_ID),
                    "Should throw exception if user is not found");

            // Assert the correct error message
            assert (exception.getMessage().equals("User not found"));
        }

        @Test
        @DisplayName("Should return empty list when user has no friends")
        void shouldReturnEmptyListWhenUserHasNoFriends() {
            // Arrange - Setup user with no friends
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act - Call service method with user having no friends
            List<FriendData> result = friendService.getAllFriendWeightLogs(USER_ID);

            // Assert - List should be empty
            assertEquals(0, result.size(), "Should return empty list when user has no friends");
        }

        @Test
        @DisplayName("Should return list with correct number of FriendData objects when user has friends")
        void shouldReturnListWithCorrectNumberOfFriendDataObjectsWhenUserHasFriends() {
            // Arrange - Setup user with two friends
            testUser.setFriends(Arrays.asList(FRIEND1_ID, FRIEND2_ID));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));
            when(userRepository.findById(FRIEND2_ID)).thenReturn(Optional.of(friend2));

            // Act - Get all friend weight logs
            List<FriendData> result = friendService.getAllFriendWeightLogs(USER_ID);

            // Assert - Should return data for both friends
            assertEquals(2, result.size(), "Should return correct number of friends");
        }

        @Test
        @DisplayName("Should return correct list of FriendData objects when user has friends")
        void shouldReturnCorrectListOfFriendDataObjectsWhenUserHasFriends() {
            // Arrange - Setup user with two friends
            testUser.setFriends(Arrays.asList(FRIEND1_ID, FRIEND2_ID));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));
            when(userRepository.findById(FRIEND2_ID)).thenReturn(Optional.of(friend2));

            // Arrange - Add weight log entries to friend1's weight log
            WeightLogEntry friend1entry1 = new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1));
            friend1.getWeightLog().add(friend1entry1);
            WeightLogEntry friend1entry2 = new WeightLogEntry(69.0, LocalDate.of(2025, 1, 2));
            friend1.getWeightLog().add(friend1entry2);

            // Arrange - Add weight log entries to friend2's weight log
            WeightLogEntry friend2entry1 = new WeightLogEntry(80.0, LocalDate.of(2025, 1, 1));
            friend2.getWeightLog().add(friend2entry1);
            WeightLogEntry friend2entry2 = new WeightLogEntry(79.0, LocalDate.of(2025, 1, 2));
            friend2.getWeightLog().add(friend2entry2);

            // Act - Get all friend weight logs
            List<FriendData> result = friendService.getAllFriendWeightLogs(USER_ID);

            // Assert - Should return correct data for both friends
            List<FriendData> expected = new ArrayList<>();
            expected.add(new FriendData(FRIEND1_ID, friend1.getUsername(), new FullName("Nora", "Jane", "Wilde"), friend1.getWeightLog()));
            expected.add(new FriendData(FRIEND2_ID, friend2.getUsername(), new FullName("Kyle", "Richard", "Anderson"), friend2.getWeightLog()));

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return correct list of FriendData objects EVEN when one friend not found")
        void shouldReturnCorrectListOfFriendDataObjectsEVENWhenOneFriendNotFound() {
            // Arrange - Setup user with two friends
            testUser.setFriends(Arrays.asList(FRIEND1_ID, FRIEND2_ID, MISSING_ID));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));
            when(userRepository.findById(FRIEND2_ID)).thenReturn(Optional.of(friend2));
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

            // Arrange - Add weight log entries to friend1's weight log
            WeightLogEntry friend1entry1 = new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1));
            friend1.getWeightLog().add(friend1entry1);
            WeightLogEntry friend1entry2 = new WeightLogEntry(69.0, LocalDate.of(2025, 1, 2));
            friend1.getWeightLog().add(friend1entry2);

            // Arrange - Add weight log entries to friend2's weight log
            WeightLogEntry friend2entry1 = new WeightLogEntry(80.0, LocalDate.of(2025, 1, 1));
            friend2.getWeightLog().add(friend2entry1);
            WeightLogEntry friend2entry2 = new WeightLogEntry(79.0, LocalDate.of(2025, 1, 2));
            friend2.getWeightLog().add(friend2entry2);

            // Act - Get all friend weight logs
            List<FriendData> result = friendService.getAllFriendWeightLogs(USER_ID);

            // Assert - Should return correct data for both friends
            List<FriendData> expected = new ArrayList<>();
            expected.add(new FriendData(FRIEND1_ID, friend1.getUsername(), new FullName("Nora", "Jane", "Wilde"), friend1.getWeightLog()));
            expected.add(new FriendData(FRIEND2_ID, friend2.getUsername(), new FullName("Kyle", "Richard", "Anderson"), friend2.getWeightLog()));

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("sendFriendRequest Tests")
    class SendFriendRequestTests {

        @Test
        @DisplayName("Should throw exception when sender not found")
        void shouldThrowExceptionWhenSenderNotFound() {
            // Arrange - Setup missing sender and existing receiver
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when sender doesn't exist
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(MISSING_ID, FRIEND1_ID),
                    "Should throw exception if sender is not found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("User not found"));
        }

        @Test
        @DisplayName("Should throw exception when receiver not found")
        void shouldThrowExceptionWhenReceiverNotFound() {
            // Arrange - Setup existing sender but missing receiver
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

            // Act & Assert - Verify exception when receiver doesn't exist
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(USER_ID, MISSING_ID),
                    "Should throw exception if receiver is not found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Receiver not found"));
        }

        // This test is perhaps superfluous but included for completeness
        @Test
        @DisplayName("Should throw exception when sender and receiver not found")
        void shouldThrowExceptionWhenSenderAndReceiverNotFound() {
            // Arrange - Setup missing sender and receiver
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
            when(userRepository.findById(MISSING_ID2)).thenReturn(Optional.empty());


            // Act & Assert - Verify exception when sender doesn't exist
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(MISSING_ID, MISSING_ID2),
                    "Should throw exception if sender and receiver not found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Neither user found"));
        }

        @Test
        @DisplayName("Should throw exception when sender and receiver are the same")
        void shouldThrowExceptionWhenSenderAndReciverAreTheSame() {
            // Arrange - No setup needed as the check occurs prior to repository calls
            // Act & Assert - Verify exception when sender and receiver are the same
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(USER_ID, USER_ID),
                    "Should throw exception if sender and receiver are the same");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Cannot send friend request to self"));
        }

        @Test
        @DisplayName("Should throw exception when sender and receiver are already friends")
        void shouldThrowExceptionWhenAlreadyFriends() {
            // Arrange - Setup users who are already friends
            testUser.getFriends().add(FRIEND1_ID);
            friend1.getFriends().add(USER_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when sender and receiver are already friends
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(USER_ID, FRIEND1_ID),
                    "Should throw exception if sender and receiver are already friends");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Users are already friends"));
        }

        @Test
        @DisplayName("Should throw exception when receiver has already sent a friend request")
        void shouldThrowExceptionWhenReceiverHasAlreadySentFriendRequest() {
            // Arrange - Setup user with existing friend request
            testUser.getFriendRequests().add(FRIEND1_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when receiver has already sent a friend request
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(USER_ID, FRIEND1_ID),
                    "Should throw exception if receiver has already sent a friend request");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("A friend request is already pending from the other user"));
        }

        @Test
        @DisplayName("Should throw exception when sender has already sent friend request")
        void shouldThrowExceptionWhenSenderHasAlreadySentFriendRequest() {
            // Arrange - Setup user with existing sent request
            testUser.getSentRequests().add(FRIEND1_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when sender has already sent a friend request
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.sendFriendRequest(USER_ID, FRIEND1_ID),
                    "Should throw exception if sender has already sent a friend request");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("A friend request has already been sent"));
        }

        @Test
        @DisplayName("Should add IDs to request lists when successful")
        void shouldAddIdsToRequestListsWhenSuccessful() {
            // Arrange - Setup users for a successful friend request
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act - Send friend request
            friendService.sendFriendRequest(USER_ID, FRIEND1_ID);

            // Assert - Verify request lists are updated correctly
            assertTrue(testUser.getSentRequests().contains(FRIEND1_ID),
                    "User's sentRequests should contain friend's ID");
            assertTrue(friend1.getFriendRequests().contains(USER_ID),
                    "Friend's friendRequests should contain user's ID");

            // Verify repository interactions
            verify(userRepository).save(testUser);
            verify(userRepository).save(friend1);
        }
    }

    @Nested
    @DisplayName("acceptFriendRequest Tests")
    class AcceptFriendRequestTests {
        @Test
        @DisplayName("Should throw exception when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - Setup missing user scenario
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Expect RuntimeException when user is missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.acceptFriendRequest(MISSING_ID, FRIEND1_ID),
                    "Should throw exception if user is not found");

            assert (exception.getMessage().equals("User not found"));
        }

        @Test
        @DisplayName("Should throw exception when requester is not found")
        void shouldThrowExceptionWhenRequesterNotFound() {
            // Arrange - Setup missing requester scenario
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when user is missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.acceptFriendRequest(USER_ID, MISSING_ID),
                    "Should throw exception if user is not found");

            assert (exception.getMessage().equals("Requester not found"));
        }

        @Test
        @DisplayName("Should throw exception when requester is not found")
        void shouldThrowExceptionWhenUserAndRequesterNotFound() {
            // Arrange - Setup missing requester scenario
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
            when(userRepository.findById(MISSING_ID2)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when user is missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.acceptFriendRequest(MISSING_ID, MISSING_ID2),
                    "Should throw exception if user is not found");

            assert (exception.getMessage().equals("Neither user found"));
        }

        @Test
        @DisplayName("Should remove IDs from sent and received lists when successful")
        void shouldRemoveIdsFromSentAndReceivedListsWhenSuccessful() {
            // Arrange - Setup users with existing friend request
            testUser.getSentRequests().add(FRIEND1_ID);
            friend1.getFriendRequests().add(USER_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act - Accept friend request
            friendService.acceptFriendRequest(FRIEND1_ID, USER_ID); // First argument is the receiver, second is the requester

            // Assert - Verify that ids are removed from request/sent lists
            assertTrue(testUser.getSentRequests().isEmpty(), "User's friends should contain friend's ID");
            assertTrue(friend1.getFriendRequests().isEmpty(), "Friend 1's friends should contain user's ID");

            // Assert - Verify repository interactions
            verify(userRepository).save(testUser);
            verify(userRepository).save(friend1);
        }

        @Test
        @DisplayName("Should throw exception when user and requester are already friends")
        void shouldThrowExceptionWhenUserAndRequesterAlreadyFriends() {
            // Arrange - Setup users who are already friends
            testUser.getFriends().add(FRIEND1_ID);
            friend1.getFriends().add(USER_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when user and requester are already friends
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.acceptFriendRequest(FRIEND1_ID, USER_ID),
                    "Should throw exception if user and requester are already friends");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Users are already friends"));
        }

        @Test
        @DisplayName("Should throw exception when no friend request found")
        void shouldThrowExceptionWhenNoFriendRequestFound() {
            // Arrange - Setup users with no friend request
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when no friend request is found
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.acceptFriendRequest(FRIEND1_ID, USER_ID),
                    "Should throw exception if no friend request is found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("No friend request found"));
        }

        @Test
        @DisplayName("Should add ids to both friends lists when successful")
        void shouldAddIdsToBothFriendsListsWhenSuccessful() {
            // Arrange - Setup users with existing friend request
            testUser.getSentRequests().add(FRIEND1_ID);
            friend1.getFriendRequests().add(USER_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act - Accept friend request
            friendService.acceptFriendRequest(FRIEND1_ID, USER_ID); // First argument is the receiver, second is the requester

            // Assert - Verify that ids are added to friends lists
            assertTrue(testUser.getFriends().contains(FRIEND1_ID), "User's friends list should contain friend's ID");
            assertTrue(friend1.getFriends().contains(USER_ID), "Friend 1's friends list should contain user's ID");

            // Assert - Verify repository interactions
            verify(userRepository).save(testUser);
            verify(userRepository).save(friend1);
        }
    }

    @Nested
    @DisplayName("getFriendRequests Tests")
    class getFriendRequestsTests {
        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - Setup missing user scenario
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when user is missing
            assertThrows(RuntimeException.class,
                    () -> friendService.getFriendRequests(MISSING_ID),
                    "Should throw exception if user is not found");
        }

        @Test
        @DisplayName("Should return empty list when user has no friend requests")
        void shouldReturnEmptyListWhenUserHasNoFriendRequests() {
            // Arrange - Setup user with no friend requests
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act - Call service method with user having no friend requests
            List<String> result = friendService.getFriendRequests(USER_ID);

            // Assert - List should be empty
            assertEquals(0, result.size(), "Should return empty list when user has no friend requests");
        }

        @Test
        @DisplayName("Should return list with correct number of friend requests when user has friend requests")
        void shouldReturnListWithCorrectNumberOfFriendRequestsWhenUserHasFriendRequests() {
            // Arrange - Setup user with two friend requests
            testUser.setFriendRequests(Arrays.asList(FRIEND1_ID, FRIEND2_ID));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act - Get all friend requests
            List<String> result = friendService.getFriendRequests(USER_ID);

            // Assert - Should return correct number of friend requests
            assertEquals(2, result.size(), "Should return correct number of friend requests");

            // Assert - Should return correct list of friend requests
            assertEquals(Arrays.asList(FRIEND1_ID, FRIEND2_ID), result);
        }
    }

    @Nested
    @DisplayName("getSentRequests Tests")
    class getSentRequestsTests {
        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - Setup missing user scenario
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when user is missing
            assertThrows(RuntimeException.class,
                    () -> friendService.getSentRequests(MISSING_ID),
                    "Should throw exception if user is not found");
        }

        @Test
        @DisplayName("Should return empty list when user has no friend requests")
        void shouldReturnEmptyListWhenUserHasNoFriendRequests() {
            // Arrange - Setup user with no sent requests
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act - Call service method with user having no friend requests
            List<String> result = friendService.getSentRequests(USER_ID);

            // Assert - List should be empty
            assertEquals(0, result.size(), "Should return empty list when user has no friend requests");
        }

        @Test
        @DisplayName("Should return list with correct number of friend requests when user has friend requests")
        void shouldReturnListWithCorrectNumberOfFriendRequestsWhenUserHasFriendRequests() {
            // Arrange - Setup user with two sent requests
            testUser.setSentRequests(Arrays.asList(FRIEND1_ID, FRIEND2_ID));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // Act - Get all friend requests
            List<String> result = friendService.getSentRequests(USER_ID);

            // Assert - Should return correct number of friend requests
            assertEquals(2, result.size(), "Should return correct number of friend requests");

            // Assert - Should return correct list of friend requests
            assertEquals(Arrays.asList(FRIEND1_ID, FRIEND2_ID), result);
        }
    }

    @Nested
    @DisplayName("removeFriend Tests")
    class removeFriendTests {
        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - Setup missing user scenario
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Expect RuntimeException when user is missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.removeFriend(MISSING_ID, FRIEND1_ID),
                    "Should throw exception if user is not found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("User not found"));
        }

        @Test
        @DisplayName("Should throw exception when friend not found")
        void shouldThrowExceptionWhenFriendNotFound() {
            // Arrange - Setup missing friend scenario
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when friend is missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.removeFriend(USER_ID, MISSING_ID),
                    "Should throw exception if friend is not found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Friend not found"));
        }

        @Test
        @DisplayName("Should throw exception when user and friend are not found")
        void shouldThrowExceptionWhenUserAndFriendNotFound() {
            // Arrange - Setup missing user and friend scenario
            when(userRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
            when(userRepository.findById(MISSING_ID2)).thenReturn(Optional.empty());

            // Act & Assert - Expect RuntimeException when user and friend are missing
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.removeFriend(MISSING_ID, MISSING_ID2),
                    "Should throw exception if user and friend are not found");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Neither user found"));
        }

        @Test
        @DisplayName("Should throw exception when user and friend are not friends")
        void shouldThrowExceptionWhenUserAndFriendAreNotFriends() {
            // Arrange - Setup users who are not friends
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act & Assert - Verify exception when user and friend are not friends
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> friendService.removeFriend(USER_ID, FRIEND1_ID),
                    "Should throw exception if user and friend are not friends");

            // Assert - the correct error message should be shown
            assert (exception.getMessage().equals("Users are not friends"));
        }

        @Test
        @DisplayName("Should remove IDs from both friends lists when successful")
        void shouldRemoveIdsFromBothFriendsListsWhenSuccessful() {
            // Arrange - Setup users who are friends
            testUser.getFriends().add(FRIEND1_ID);
            friend1.getFriends().add(USER_ID);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.findById(FRIEND1_ID)).thenReturn(Optional.of(friend1));

            // Act - Remove friend
            friendService.removeFriend(USER_ID, FRIEND1_ID);

            // Assert - Verify that ids are removed from friends lists
            assertFalse(testUser.getFriends().contains(FRIEND1_ID), "User's friends list should not contain friend's ID");
            assertFalse(friend1.getFriends().contains(USER_ID), "Friend's friends list should not contain user's ID");

            // Assert - Verify repository interactions
            verify(userRepository).save(testUser);
            verify(userRepository).save(friend1);
        }
    }
}