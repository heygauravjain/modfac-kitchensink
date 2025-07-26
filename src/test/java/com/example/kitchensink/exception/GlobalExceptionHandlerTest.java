package com.example.kitchensink.exception;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.kitchensink.controller.RestService;
import com.example.kitchensink.security.JwtRequestFilter;
import com.example.kitchensink.security.JwtTokenUtil;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.WebRequest;

@WebMvcTest(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @Mock
  private WebRequest webRequest;

  @MockBean
  private JwtTokenUtil jwtTokenUtil;

  @MockBean
  private JwtRequestFilter jwtRequestFilter;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(
            new RestService(null)) // Initialize mockMvc with any controller
        .setControllerAdvice(globalExceptionHandler) // Register the exception handler with mockMvc
        .build();

    // Mock the request description
    when(webRequest.getDescription(false)).thenReturn("Mock request description");
  }
  @Test
  void handleResourceNotFoundException_WithCustomMessage() {
    // Arrange: Create a custom ResourceNotFoundException
    ResourceNotFoundException ex = new ResourceNotFoundException("Custom not found message");

    // Act: Call the exception handler directly
    ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFoundException(ex,
        webRequest);

    // Assert: Verify the response status and message
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof Map);
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("Custom not found message", body.get("message"));
    assertEquals("Mock request description", body.get("details"));
  }

  @Test
  void handleAllExceptions_WithDefaultMessage() {
    // Arrange: Create a general exception with a specific message
    Exception ex = new Exception("Test exception message");

    // Act: Call the exception handler directly
    ResponseEntity<Object> response = globalExceptionHandler.handleAllExceptions(ex, webRequest);

    // Assert: Verify the response status and message
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof Map);
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("An unexpected error occurred", body.get("message"));
    assertEquals("Test exception message", body.get("details"));
  }
}
