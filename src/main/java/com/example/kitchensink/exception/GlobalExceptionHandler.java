package com.example.kitchensink.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

  // Handle ResourceNotFoundException
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public final ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
      WebRequest request) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("details", request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  // Add specific handler for static resource errors
  @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public final ResponseEntity<Object> handleNoHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException ex,
      WebRequest request) {
    log.warn("Static resource not found: {} - Request: {}", ex.getRequestURL(), request.getDescription(false));
    
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("message", "An unexpected error occurred");
    errorDetails.put("details", "No static resource " + ex.getRequestURL().substring(ex.getRequestURL().lastIndexOf("/") + 1) + ".");
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
    log.error("NullPointerException occurred: {}", ex.getMessage(), ex);
    
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("message", "An unexpected error occurred");
    errorDetails.put("details", ex.getMessage());
    
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("IllegalArgumentException occurred: {}", ex.getMessage(), ex);
    
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("message", "Invalid request");
    errorDetails.put("details", ex.getMessage());
    
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  // Handle all other exceptions
  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("message", "An unexpected error occurred");
    errorDetails.put("details", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex,
      WebRequest request) {
    Map<String, Object> errorDetails = new HashMap<>();
    Map<String, String> validationErrors = new HashMap<>();

    // Collect validation errors
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      validationErrors.put(error.getField(), error.getDefaultMessage());
    }

    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("message", "Validation failed");
    errorDetails.put("details", request.getDescription(false));
    errorDetails.put("validationErrors", validationErrors);

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
}
