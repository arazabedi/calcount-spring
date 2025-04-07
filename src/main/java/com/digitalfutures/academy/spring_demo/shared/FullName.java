package com.digitalfutures.academy.spring_demo.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class FullName {
    @Getter
    @JsonProperty("first_name")
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 1, max = 30, message = "First name must be between 1 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;

    @JsonProperty("middle_name")
    @Size(max = 30, message = "Middle name must be less than 30 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']*$", message = "Middle name can only contain letters, spaces, hyphens, and apostrophes")
    private String middleName;  // Optional, so no @NotEmpty

    @JsonProperty("last_name")
    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 1, max = 30, message = "Last name must be between 1 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    public String getFullName() {
        return String.format("%s %s %s", firstName, middleName, lastName);
    }
}
