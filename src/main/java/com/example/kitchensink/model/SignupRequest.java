package com.example.kitchensink.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Name is required")
    @Size(min = ValidationPatterns.NAME_MIN_LENGTH, max = ValidationPatterns.NAME_MAX_LENGTH, message = "Name must be between 1 and 25 characters")
    @Pattern(regexp = ValidationPatterns.NAME_PATTERN, message = ValidationPatterns.NAME_MESSAGE)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = ValidationPatterns.PASSWORD_MIN_LENGTH, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ValidationPatterns.PASSWORD_PATTERN, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String password;
    
    // Optional phone number - only validate if not empty
    @Pattern(regexp = ValidationPatterns.PHONE_PATTERN, message = ValidationPatterns.PHONE_MESSAGE)
    private String phoneNumber;
    
    private String role = "USER"; // Default role
} 