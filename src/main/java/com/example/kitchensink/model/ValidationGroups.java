package com.example.kitchensink.model;

/**
 * Validation groups to allow different validation scenarios.
 * This enables conditional validation based on the operation being performed.
 */
public interface ValidationGroups {
    
    /**
     * Validation group for creating new entities
     */
    interface Create {}
    
    /**
     * Validation group for updating existing entities
     */
    interface Update {}
    
    /**
     * Validation group for basic operations (minimal validation)
     */
    interface Basic {}
} 