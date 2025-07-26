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
