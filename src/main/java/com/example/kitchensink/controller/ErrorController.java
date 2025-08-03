package com.example.kitchensink.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/error")
@Slf4j
public class ErrorController {

    /**
     * Handles 500 Internal Server Error
     *
     * @param request the HTTP request
     * @param model the model
     * @param error the error message
     * @param message the custom message
     * @param timestamp the error timestamp
     * @return the 500 error page view
     */
    @GetMapping("/500")
    public String handle500Error(HttpServletRequest request, 
                                Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "message", required = false) String message,
                                @RequestParam(value = "timestamp", required = false) String timestamp) {
        
        log.error("500 error occurred for request: {}", request.getRequestURI());
        
        // Set default values if not provided
        if (error == null) {
            error = "Internal Server Error";
        }
        if (message == null) {
            message = "An unexpected error occurred";
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now().toString();
        }
        
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        model.addAttribute("timestamp", timestamp);
        
        return "error/500";
    }

    /**
     * Handles 404 Not Found Error
     *
     * @param request the HTTP request
     * @param model the model
     * @return the 404 error page view
     */
    @GetMapping("/404")
    public String handle404Error(HttpServletRequest request, Model model) {
        log.warn("404 error occurred for request: {}", request.getRequestURI());
        model.addAttribute("error", "Page Not Found");
        model.addAttribute("message", "The requested page could not be found");
        model.addAttribute("timestamp", LocalDateTime.now().toString());
        return "error/404";
    }

    /**
     * Handles static resource not found errors
     *
     * @param request the HTTP request
     * @param model the model
     * @return the 404 error page view
     */
    @GetMapping("/static-resource-error")
    public String handleStaticResourceError(HttpServletRequest request, Model model) {
        String resourcePath = request.getParameter("resource");
        log.warn("Static resource not found: {} - Request: {}", resourcePath, request.getRequestURI());
        
        model.addAttribute("error", "Resource Not Found");
        model.addAttribute("message", "The requested resource could not be found");
        model.addAttribute("details", "No static resource " + (resourcePath != null ? resourcePath : "unknown") + ".");
        model.addAttribute("timestamp", LocalDateTime.now().toString());
        return "error/404";
    }

    /**
     * Handles generic errors
     *
     * @param request the HTTP request
     * @param model the model
     * @param error the error message
     * @param message the custom message
     * @param timestamp the error timestamp
     * @return the appropriate error page view
     */
    @GetMapping("")
    public String handleGenericError(HttpServletRequest request, 
                                   Model model,
                                   @RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "message", required = false) String message,
                                   @RequestParam(value = "timestamp", required = false) String timestamp) {
        
        log.error("Generic error occurred for request: {}", request.getRequestURI());
        
        // Set default values if not provided
        if (error == null) {
            error = "Server Error";
        }
        if (message == null) {
            message = "An unexpected error occurred";
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now().toString();
        }
        
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        model.addAttribute("timestamp", timestamp);
        
        return "error/500";
    }
} 