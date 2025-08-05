package com.example.kitchensink.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private ErrorController errorController;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/test/endpoint");
    }

    @Test
    void handle500Error_WithAllParameters_ShouldSetAllAttributes() {
        // Given
        String error = "Custom Error";
        String message = "Custom Message";
        String timestamp = "2023-01-01T10:00:00";

        // When
        String viewName = errorController.handle500Error(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq(error));
        verify(model).addAttribute(eq("message"), eq(message));
        verify(model).addAttribute(eq("timestamp"), eq(timestamp));
    }

    @Test
    void handle500Error_WithNullError_ShouldSetDefaultError() {
        // Given
        String error = null;
        String message = "Custom Message";
        String timestamp = "2023-01-01T10:00:00";

        // When
        String viewName = errorController.handle500Error(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq("Internal Server Error"));
        verify(model).addAttribute(eq("message"), eq(message));
        verify(model).addAttribute(eq("timestamp"), eq(timestamp));
    }

    @Test
    void handle500Error_WithNullMessage_ShouldSetDefaultMessage() {
        // Given
        String error = "Custom Error";
        String message = null;
        String timestamp = "2023-01-01T10:00:00";

        // When
        String viewName = errorController.handle500Error(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq(error));
        verify(model).addAttribute(eq("message"), eq("An unexpected error occurred"));
        verify(model).addAttribute(eq("timestamp"), eq(timestamp));
    }

    @Test
    void handle500Error_WithNullTimestamp_ShouldSetCurrentTimestamp() {
        // Given
        String error = "Custom Error";
        String message = "Custom Message";
        String timestamp = null;

        // When
        String viewName = errorController.handle500Error(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq(error));
        verify(model).addAttribute(eq("message"), eq(message));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }

    @Test
    void handle500Error_WithAllNullParameters_ShouldSetAllDefaults() {
        // Given
        String error = null;
        String message = null;
        String timestamp = null;

        // When
        String viewName = errorController.handle500Error(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq("Internal Server Error"));
        verify(model).addAttribute(eq("message"), eq("An unexpected error occurred"));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }

    @Test
    void handle404Error_ShouldSetCorrectAttributes() {
        // When
        String viewName = errorController.handle404Error(request, model);

        // Then
        assertEquals("error/404", viewName);
        verify(model).addAttribute(eq("error"), eq("Page Not Found"));
        verify(model).addAttribute(eq("message"), eq("The requested page could not be found"));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }

    @Test
    void handleStaticResourceError_WithResourceParameter_ShouldSetCorrectAttributes() {
        // Given
        when(request.getParameter("resource")).thenReturn("/css/style.css");

        // When
        String viewName = errorController.handleStaticResourceError(request, model);

        // Then
        assertEquals("error/404", viewName);
        verify(model).addAttribute(eq("error"), eq("Resource Not Found"));
        verify(model).addAttribute(eq("message"), eq("The requested resource could not be found"));
        verify(model).addAttribute(eq("details"), eq("No static resource /css/style.css."));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }

    @Test
    void handleStaticResourceError_WithNullResourceParameter_ShouldSetUnknownResource() {
        // Given
        when(request.getParameter("resource")).thenReturn(null);

        // When
        String viewName = errorController.handleStaticResourceError(request, model);

        // Then
        assertEquals("error/404", viewName);
        verify(model).addAttribute(eq("error"), eq("Resource Not Found"));
        verify(model).addAttribute(eq("message"), eq("The requested resource could not be found"));
        verify(model).addAttribute(eq("details"), eq("No static resource unknown."));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }

    @Test
    void handleGenericError_WithAllParameters_ShouldSetAllAttributes() {
        // Given
        String error = "Custom Error";
        String message = "Custom Message";
        String timestamp = "2023-01-01T10:00:00";

        // When
        String viewName = errorController.handleGenericError(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq(error));
        verify(model).addAttribute(eq("message"), eq(message));
        verify(model).addAttribute(eq("timestamp"), eq(timestamp));
    }

    @Test
    void handleGenericError_WithNullError_ShouldSetDefaultError() {
        // Given
        String error = null;
        String message = "Custom Message";
        String timestamp = "2023-01-01T10:00:00";

        // When
        String viewName = errorController.handleGenericError(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq("Server Error"));
        verify(model).addAttribute(eq("message"), eq(message));
        verify(model).addAttribute(eq("timestamp"), eq(timestamp));
    }

    @Test
    void handleGenericError_WithNullMessage_ShouldSetDefaultMessage() {
        // Given
        String error = "Custom Error";
        String message = null;
        String timestamp = "2023-01-01T10:00:00";

        // When
        String viewName = errorController.handleGenericError(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq(error));
        verify(model).addAttribute(eq("message"), eq("An unexpected error occurred"));
        verify(model).addAttribute(eq("timestamp"), eq(timestamp));
    }

    @Test
    void handleGenericError_WithNullTimestamp_ShouldSetCurrentTimestamp() {
        // Given
        String error = "Custom Error";
        String message = "Custom Message";
        String timestamp = null;

        // When
        String viewName = errorController.handleGenericError(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq(error));
        verify(model).addAttribute(eq("message"), eq(message));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }

    @Test
    void handleGenericError_WithAllNullParameters_ShouldSetAllDefaults() {
        // Given
        String error = null;
        String message = null;
        String timestamp = null;

        // When
        String viewName = errorController.handleGenericError(request, model, error, message, timestamp);

        // Then
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq("Server Error"));
        verify(model).addAttribute(eq("message"), eq("An unexpected error occurred"));
        verify(model).addAttribute(eq("timestamp"), anyString());
    }
} 