package com.digitalfutures.academy.spring_demo.unit.model;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.service.PasswordService;
import com.digitalfutures.academy.spring_demo.shared.FullName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// This class mainly tests validation constraints on the User model
@ExtendWith(MockitoExtension.class)
class UserTest {
    // Validator class checks that User objects are valid given constraints defined in the User model.
    private Validator validator;

    // Mock to allow for stubbing of the hashPassword method
    @Mock
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        // Create a ValidatorFactory instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // Create a Validator instance
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValidWhenAllFieldsAreValid() {
        // Arrange - Create a valid User object
        User user = new User("noraarmstrong", new FullName("Nora", "Emilia", "Armstrong"), "noraarmstrong@gmail.com");

        // Act - Get the set of constraint violations of the User object
        Set<ConstraintViolation<User>> violations = validator.validate(user); // Returns a Set object containing any constraint violations

        // Assert - Check that the User object is valid
        assertTrue(violations.isEmpty(), "User should be valid when all fields meet constraints");
    }

    @Test
    void userShouldBeInvalidWhenUsernameIsEmpty() {
        // Arrange - Create a User object with an empty username
        User user = new User("", new FullName("Nora", "Emilia", "Armstrong"), "noraarmstrong@gmail.com");

        // Act - Get the set of constraint violations of the User object
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert - Check that validation set contains a constraint violation
        assertFalse(violations.isEmpty(), "User should be invalid when username is empty");
    }

    @Test
    void userShouldBeInvalidWhenEmailIsInvalid() {
        // Arrange - Create a User object with an invalid (empty) email
        User user = new User("noraarmstrong", new FullName("Nora", "Emilia", "Armstrong"), "");

        // Act - Get the set of constraint violations of the User object
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert - Check that validation set contains a constraint violation
        assertFalse(violations.isEmpty(), "User should be invalid when email format is incorrect");
    }

    @Test
    void userShouldBeInvalidWhenFullNameIsNull() {
        // Arrange - Create a User object with a null full name
        User user = new User("noraarmstrong", null, "noraarmstrong@gmail.com");

        // Act - Get the set of constraint violations of the User object
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert - Check that validation set contains a constraint violation
        assertFalse(violations.isEmpty(), "User should be invalid when full name is null");
    }

    @Test
    void setHashedPasswordShouldStoreHashedPassword() {
        // Arrange - Create a User object
        User user = new User("validUser", new FullName("Nora", "Emilia", "Armstrong"), "noraarmstrong@gmail.com");

        // Arrange - Stub the hashPassword method to return a hashed password
        when(passwordService.hashPassword("123456")).thenReturn("123456hashed");

        // Act - Call the setHashedPassword method
        user.setHashedPassword("123456", passwordService);

        // Assert - Check that the hashed password was set and the passwordService called
        verify(passwordService, times(1)).hashPassword("123456");
        assertEquals("123456hashed", user.getHashedPassword());
    }
}
