package com.digitalfutures.academy.spring_demo.unit.service;

import com.digitalfutures.academy.spring_demo.dto.response.RegistrationResponse;
import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.service.PasswordService;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // Mock dependencies to stub their methods
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    // Capture the arguments passed to the mocked dependencies
    // This is automatically reset before every test - hence do not need to put it in the setUp method
    @Captor
    private ArgumentCaptor<User> userCaptor;

    // Instantiate userService and inject mocks
    @InjectMocks
    private UserService userService;

    // Test data constants for improved readability and maintenance
    private static final String USER_ID = "123";
    private static final String FRIEND1_ID = "456";
    private static final String FRIEND2_ID = "789";

    // Fields used for testing
    private final String testUsername = "noraarmstrong";
    private final FullName testFullName = new FullName("Nora", "Emilia", "Armstrong");
    private final String testEmail = "noraarmstrong@gmail.com";
    private final String testRawPassword = "123456";
    private final String testHashedPassword = "hashed123456";
    private final String testUserId = "67c0a11dc17a8e3cc89ff4fb";

    // User objects to be tested
    private User testUser;

    @BeforeEach
    void setupBaseUsers() {
        // Create a test user before each test to keep test code DRY
        testUser = new User();
    }

    // Perhaps redundant - the service method just calls the repository method
    @Nested
    @DisplayName("getAllUsers tests")
    class getAllUsersTests {
        @Test
        @DisplayName("Should call userRepository findAll method")
        void shouldCallRepositoryMethod() {
            // Act - Call the getAllUsers method
            userService.getAllUsers();

            // Assert - Verify that userRepository.findAll was called
            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("registerUser tests")
    class registerUserTests {

        // This test could be split into a hash password test and a save user test but I think it's fine as it is
        @Test
        @DisplayName("Should hash password and save user to database when successful")
        void shouldHashPasswordAndSaveUserToDatabase() {
            // Arrange - Stub the passwordService to always return the predetermined hashed password
            when(passwordService.hashPassword(testRawPassword)).thenReturn(testHashedPassword);
            // Act - Call the registerUser method
            userService.registerUser(testUsername, testFullName, testEmail, testRawPassword);

            // Assert - Verify that the user was saved and password was hashed
            verify(passwordService, times(1)).hashPassword(testRawPassword);
            verify(userRepository, times(1)).save(userCaptor.capture()); // userCaptor.capture() captures the argument passed to the save method

            // Assert - Retrieve the user and verify that the hashed password was set
            User user = userCaptor.getValue(); // userCaptor.getValue() returns the argument passed to the save method
            assertEquals(testHashedPassword, user.getHashedPassword());

            // Assert - Verify that the user object has the correct fields
            assertEquals(testUsername, user.getUsername());
            assertEquals(testFullName, user.getFullName());
            assertEquals(testEmail, user.getEmail());
        }

        @Test
        @DisplayName("Should return the RegistrationRequest dto representation of the user when successful")
        void shouldReturnRegistrationRequestDto() {
            // Arrange - Stub the passwordService to always return the predetermined hashed password
            when(passwordService.hashPassword(testRawPassword)).thenReturn(testHashedPassword);

            // Arrange - (stub setId) When userRepository.save is called, set the user's id to testUserId.
            doAnswer(invocation -> {
                User user = invocation.getArgument(0); // Get the first argument passed to the save method (i.e. the user)
                user.set_id(testUserId); // Set the user's id to testUserId.
                return null; // Return null as the setId method is void.
            }).when(userRepository).save(any(User.class)); // Do the above when userRepository.save is called with any User object

            // Arrange - Create the expected RegistrationResponse object
            User expectedUser = new User(testUsername, testFullName, testEmail);
            expectedUser.set_id(testUserId);
            RegistrationResponse expected = new RegistrationResponse(expectedUser);

            // Act - Call the registerUser method
            RegistrationResponse response = userService.registerUser(testUsername, testFullName, testEmail, testRawPassword);

            // Assert - compare the expected and actual responses
            assertEquals(expected, response);
        }

        @Test
        @DisplayName("Should throw an exception when the user already exists")
        void shouldThrowExceptionWhenUserAlreadyExists() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.existsByUsername(testUsername)).thenReturn(true);

            // Act and Assert - Verify that an exception is thrown when the user already exists
            assertThrows(RuntimeException.class, () -> userService.registerUser(testUsername, testFullName, testEmail, testRawPassword));
        }
    }

    @Nested
    @DisplayName("authenticateUser tests")
    class authenticateUserTests {
        @Test
        @DisplayName("Should return true when username and password match")
        void shouldReturnTrueWhenUsernameAndPasswordMatch() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findByUsername(testUsername)).thenReturn(testUser);

            // Arrange - Stub the passwordService to return true when verifyPassword is called
            when(passwordService.verifyPassword(testRawPassword, testUser.getHashedPassword())).thenReturn(true);

            // Act - Call the authenticateUser method
            boolean result = userService.authenticateUser(testUsername, testRawPassword);

            // Assert - Verify that the result is true
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when username and password don't match")
        void shouldReturnFalseWhenUsernameAndPasswordDoNotMatch() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findByUsername(testUsername)).thenReturn(testUser);

            // Act - Call the authenticateUser method
            boolean result = userService.authenticateUser(testUsername, testRawPassword);

            // Assert - Verify that the result is false
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when username is not found")
        void shouldReturnFalseWhenUsernameIsNotFound() {
            // Arrange - Stub the userRepository to return null when findByUsername is called
            when(userRepository.findByUsername(testUsername)).thenReturn(null);

            // Act - Call the authenticateUser method
            boolean result = userService.authenticateUser(testUsername, testRawPassword);

            // Assert - Verify that the result is false
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when password is incorrect")
        void shouldReturnFalseWhenPasswordIsIncorrect() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findByUsername(testUsername)).thenReturn(testUser);

            // Act - Call the authenticateUser method
            boolean result = userService.authenticateUser(testUsername, "wrongPassword");

            // Assert - Verify that the result is false
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when password is null")
        void shouldReturnFalseWhenPasswordIsNull() {
            // Act - Call the authenticateUser method
            boolean result = userService.authenticateUser(testUsername, null);

            // Assert - Verify that the result is false
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when username is null")
        void shouldReturnFalseWhenUsernameIsNull() {
            // Act - Call the authenticateUser method
            boolean result = userService.authenticateUser(null, testRawPassword);

            // Assert - Verify that the result is false
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getWeightLog tests")
    class getWeightLog {
        @Test
        @DisplayName("Should return weight log entries for a user")
        void shouldReturnWeightLogEntriesForAUser() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            testUser.getWeightLog().add(new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1)));
            testUser.getWeightLog().add(new WeightLogEntry(69.0, LocalDate.of(2025, 1, 2)));

            // Act - Call the getWeightLogByUsername method
            List<WeightLogEntry> result = userService.getWeightLog(testUserId);

            // Assert - Create a list of expected weight log entries
            List<WeightLogEntry> expected = new ArrayList<>();
            expected.add(new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1)));
            expected.add(new WeightLogEntry(69.0, LocalDate.of(2025, 1, 2)));

            // Assert - Verify that the weight log entries are returned
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - Stub the userRepository to return null when findByUsername is called
            when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

            // Act and Assert - Verify that an exception is thrown when the user is not found
            assertThrows(RuntimeException.class, () -> userService.getWeightLog(testUserId));
        }

        @Test
        @DisplayName("Should return empty list when user has no weight log")
        void shouldReturnEmptyListWhenUserHasNoWeightLogEntries() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // Act - Call the getWeightLogByUsername method
            List<WeightLogEntry> result = userService.getWeightLog(testUserId);

            // Assert - Verify that the weight log is empty
            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("addWeightLogEntry tests")
    class addWeightLogEntryTests {
        @Test
        @DisplayName("Should add weight log entry to user's weight log")
        void shouldAddWeightLogEntryToUserWeightLog() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // Act - Call the addWeightLogEntry method
            userService.addWeightLogEntry(testUserId, new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1)));

            // Assert - Create a list of expected weight log entries
            List<WeightLogEntry> expected = new ArrayList<>();
            expected.add(new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1)));

            // Assert - Check that the expected weight log matches the actual weight log
            assertEquals(expected, testUser.getWeightLog());
        }

        // This test could be included in the shouldAddWeightLogEntryToUserWeightLog test, but it's added for clarity
        @Test
        @DisplayName("Should save updated user to database")
        void shouldSaveUpdatedUserToDatabase() {
            // Arrange - Stub the userRepository to return a user when findByUsername is called
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // Act - Call the addWeightLogEntry method
            userService.addWeightLogEntry(testUserId, new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1)));

            // Assert - Verify that the weight log entry was added to the user's weight log and saved
            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange - simulate a user not found
            when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

            // Act and Assert - Verify that an exception is thrown when the user is not found
            assertThrows(RuntimeException.class, () -> userService.addWeightLogEntry(testUserId, new WeightLogEntry(70.0, LocalDate.of(2025, 1, 1))));
        }

        @Test
        @DisplayName("Should throw exception when weight log entry is null")
        void shouldThrowExceptionWhenWeightLogEntryIsNull() {
            // Arrange - Ensure that the exception isn't thrown due to a missing user
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // Act and Assert - Verify that an exception is thrown when the weight log entry is null
            assertThrows(RuntimeException.class, () -> userService.addWeightLogEntry(testUserId, null));
        }

        @Test
        @DisplayName("Should throw exception when weight log entry is empty")
        void shouldThrowExceptionWhenWeightLogEntryIsEmpty() {
            // Arrange - Ensure that the exception isn't thrown due to a missing user
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // Arrange - Create an empty weight log entry
            WeightLogEntry emptyEntry = new WeightLogEntry();

            // Act and Assert - Verify that an exception is thrown when the weight log entry is null
            assertThrows(RuntimeException.class, () -> userService.addWeightLogEntry(testUserId, emptyEntry));
        }
    }

    @Nested
    @DisplayName("searchByUsername Tests")
    class searchByUsernameTests {
        @Test
        @DisplayName("Should return user when a a valid username is provided")
        void shouldReturnUserWhenPartialOfValidUsernameIsProvided() {
            // Arrange - Stub the userRepository to return a user when findByUsernameContaining is called
            when(userRepository.findByUsernameContaining("noraarmstrong")).thenReturn(Arrays.asList(testUser));

            // Act - Call the findByUsernameContaining method
            List<User> result = userService.searchByUsername("noraarmstrong");

            System.out.println(result.size());
            // Assert - Verify that the user was returned
            assertTrue(result.contains(testUser));
        }

        @Test
        @DisplayName("Should call userRepository findByUsernameContaining method")
        void shouldCallRepositoryMethod() {
            // Act - Call the findByUsernameContaining method
            userService.searchByUsername("noraarmstrong");

            // Assert - Verify that userRepository.findByUsernameContaining was called
            verify(userRepository, times(1)).findByUsernameContaining(anyString());
        }

        @Test
        @DisplayName("Should return empty list when username does not exist")
        void shouldReturnEmptyListWhenUsernameDoesNotExist() {
            // Act - Call the findByUsernameContaining method
            List<User> result = userService.searchByUsername("jamesmarshall");

            // Assert - Verify that the user was returned
            assertEquals(0, result.size(), "Should return empty list when username does not exist");
        }
    }
}