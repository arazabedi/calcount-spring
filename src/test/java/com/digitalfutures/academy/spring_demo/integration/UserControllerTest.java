package com.digitalfutures.academy.spring_demo.integration;

import com.digitalfutures.academy.spring_demo.CalCountApplication;
import com.digitalfutures.academy.spring_demo.TestMongoConfig;
import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.repositories.UserRepository;
import com.digitalfutures.academy.spring_demo.shared.WeightLogEntry;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = CalCountApplication.class)
// Does not interact with the database - instead, uses a mock repository (UserRepository)
// Therefore don't worry about the database being affected by these tests
@AutoConfigureMockMvc
public class UserControllerTest {

    // Object used to simulate HTTP requests and responses for integration testing
    @Autowired
    private MockMvc mockMvc;

    // Mock repository used to simulate database interactions
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setupUsers() {
        // Ensure empty userRepository before each test
        userRepository.deleteAll();
        // Create and save the test user
        User testUser = new User();
        // Give the user a username
        testUser.setUsername("noraarmstrong");
        // Save the user to the userRepository
        userRepository.save(testUser);

        // Manually set the SecurityContext to use the testUser as the principal to bypass token authentication
        // This is necessary because the @WithMockUser annotation doesn't set the user as the principal
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                testUser, // Principal (the User object)
                null, // Credentials (not needed)
                Collections.emptyList() // Authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    @DisplayName("GET - getWeightLog tests")
    class getWeightLogTests {
        @Test
        @DisplayName("Should return status code of 200 when user found")
        void shouldReturn200HttpStatusCodeWhenUserIsFound() throws Exception {
            // Act & Assert - Perform the request and verify status code is 200
            mockMvc.perform(get("/api/user/weight-log"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return status code 404 when user is not found")
        void shouldReturnNotFoundHttpStatusCodeWhenUserIsNotFound() throws Exception {
            // Arrange - Delete the user from the repository
            userRepository.deleteByUsername("noraarmstrong");

            // Act & Assert - Perform the request and verify a 404 (Not Found) status code
            mockMvc.perform(get("/api/user/weight-log"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET - Should return correct weight log entries")
        void shouldReturnCorrectWeightLogEntries() throws Exception {
            // Arrange - Retrieve the user from the repository
            User user = userRepository.findByUsername("noraarmstrong");

            // Arrange - Add weight logs
            WeightLogEntry entry1 = new WeightLogEntry(75.0, LocalDate.of(2025, 1, 1));
            WeightLogEntry entry2 = new WeightLogEntry(76.5, LocalDate.of(2025, 1, 2));
            user.getWeightLog().add(entry1);
            user.getWeightLog().add(entry2);

            userRepository.save(user);

            // Act & Assert - Perform the request and verify correct weight log entries are returned
            mockMvc.perform(get("/api/user/weight-log"))
                    // Status ok is explicitly tested in the previous test - it's just here to show the expected response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weightLog").isArray())
                    .andExpect(jsonPath("$.weightLog.length()").value(2))
                    .andExpect(jsonPath("$.weightLog[0].weight").value(75.0))
                    .andExpect(jsonPath("$.weightLog[1].weight").value(76.5));
        }

        @Test
        @DisplayName("Should return empty array when user has no weight log entries")
        void shouldReturnEmptyArrayWhenUserHasNoWeightLogEntries() throws Exception {
            // Act & Assert - Perform the request and verify empty weight log array is returned
            mockMvc.perform(get("/api/user/weight-log"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weightLog").isArray())
                    .andExpect(jsonPath("$.weightLog.length()").value(0));
        }
    }

    @Nested
    @DisplayName("POST - postWeightLogEntry tests")
    class PostWeightLogEntryTests {
        @Test
        @DisplayName("Should return status code 200 and success message when weight log entry is added")
        void shouldAddWeightLogEntry() throws Exception {
            // Act - Perform the request with a valid weight log entry and verify 200 with success message
            mockMvc.perform(post("/api/user/weight-log")
                            .contentType("application/json")
                            .content("{\"weight\": 75.0, \"date\": \"2025-01-01\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Weight log entry added successfully"));
        }

        @Test
        @DisplayName("Should add weight log entry to user's weight log")
        void shouldAddWeightLogEntryToUserWeightLog() throws Exception {
            // Act - Perform the request w
            mockMvc.perform(post("/api/user/weight-log")
                    .contentType("application/json")
                    .content("{\"weight\": 75.0, \"date\": \"2025-01-01\"}"));


            //  Assert - Get the weight log entry from the userRepository and verify it's as expected
            WeightLogEntry expected = new WeightLogEntry(75.0, LocalDate.of(2025, 1, 1));
            User user = userRepository.findByUsername("noraarmstrong");
            WeightLogEntry weightLogEntry = user.getWeightLog().get(0);
            assertEquals(expected, weightLogEntry);
        }

        @Test
        @DisplayName("Should return status code 404 when weight log entry is empty")
        void shouldReturnBadRequestWhenWeightLogEntryIsEmpty() throws Exception {
            mockMvc.perform(post("/api/user/weight-log")
                            .contentType("application/json")
                            .content("{\"weight\": null, \"date\": null}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should not save weight log entry to database when weight log entry is empty")
        void shouldNotSaveWeightLogEntryWhenWeightLogEntryIsEmpty() throws Exception {
            mockMvc.perform(post("/api/user/weight-log")
                    .contentType("application/json")
                    .content("{\"weight\": null, \"date\": null}")).andExpect(status().isBadRequest());

            // Verify that the weight log entry was not saved to the database
            User user = userRepository.findByUsername("noraarmstrong");
            assertEquals(0, user.getWeightLog().size(), "Weight log entry should not be saved to database");
        }

        @Test
        @DisplayName("Should return status code 404 when user is not found")
        void shouldReturnNotFoundHttpStatusCodeWhenUserIsNotFound() throws Exception {
            // Arrange - Delete the user from the repository
            userRepository.deleteByUsername("noraarmstrong");

            // Act & Assert - Perform the request and verify a 404 (Not Found) status code
            mockMvc.perform(post("/api/user/weight-log")
                            .contentType("application/json")
                            .content("{\"weight\": 75.0, \"date\": \"2025-01-01\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET - searchForUserByUsername tests")
    class SearchForUserByUsernameTests {
        @Test
        @DisplayName("Should return status code 200 when params are empty")
        void shouldReturn200StatusCodeWhenParamsEmpty() throws Exception {
            mockMvc.perform(get("/api/users/search?username="))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return status code 200 when params are not empty")
        void shouldReturn200StatusCodeWhenParamsNotEmpty() throws Exception {
            mockMvc.perform(get("/api/users/search?username=nora"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return user when username exists")
        void shouldReturnUserWhenUsernameExists() throws Exception {
            // Arrange - Create and save a user
            User randomUser = new User();
            randomUser.setUsername("archiebaldwin");
            userRepository.save(randomUser);

            // Act & Assert - Perform the request and verify the response
            mockMvc.perform(get("/api/users/search?username=archiebaldwin"))
                    .andExpect(jsonPath("$[0].username").value("archiebaldwin"));
        }

        @Test
        @DisplayName("Should return user when passing a valid partial username")
        void shouldReturnUserWhenPassingAValidPartialUsername() throws Exception {
            // Arrange - Create and save a user
            User randomUser = new User();
            randomUser.setUsername("jeremiahdalton");
            userRepository.save(randomUser);

            // Act & Assert - Perform the request and verify the response
            mockMvc.perform(get("/api/users/search?username=jer"))
                    .andExpect(jsonPath("$[0].username").value("jeremiahdalton"));
        }
    }

    // The endpoint being tested below is for development purposes only - it returns all users in the database
    @Nested
    @DisplayName("GET - GetAllUsers tests")
    class GetAllUsersTests {
        @Test
        @DisplayName("Should return status code of okay when all the users are found")
        void shouldReturnOkayHttpStatusCodeWhenAllUsersAreFound() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk());
        }
    }
}
