package com.example.kitchensink.model;

/**
 * Centralized validation patterns to avoid duplication across model classes.
 * This class contains common regex patterns and validation constants.
 */
public final class ValidationPatterns {
    
    // Private constructor to prevent instantiation
    private ValidationPatterns() {}
    
    // Phone number validation: 10-12 digits or empty
    public static final String PHONE_PATTERN = "^$|^\\d{10,12}$";
    public static final String PHONE_MESSAGE = "Phone number must be 10-12 digits or empty";
    
    // Name validation: no numbers allowed
    public static final String NAME_PATTERN = "[^0-9]*";
    public static final String NAME_MESSAGE = "Name must not contain numbers";
    
    // Password validation: at least 8 characters
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String PASSWORD_MESSAGE = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character";
    
    // Simple password validation (for backward compatibility)
    public static final String SIMPLE_PASSWORD_MESSAGE = "Password must be at least 8 characters long";
    
    // Validation size constraints
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 25;
    public static final int PASSWORD_MIN_LENGTH = 8;
} 