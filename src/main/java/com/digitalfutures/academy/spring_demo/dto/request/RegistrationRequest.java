package com.digitalfutures.academy.spring_demo.dto.request;

import com.digitalfutures.academy.spring_demo.shared.FullName;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;

    @JsonProperty("full_name")
    @NotNull(message = "Full name is required")
    @Valid
    private FullName fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 50, message = "Email must be less than 50 characters")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    private String password;
}
