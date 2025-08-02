package com.example.kitchensink.controller;

import com.example.kitchensink.model.AuthRequest;
import com.example.kitchensink.model.SignupRequest;
import com.example.kitchensink.security.JwtTokenService;
import com.example.kitchensink.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class JwtAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final AuthService authService;

    @GetMapping("/jwt-login")
    public String showJwtLoginPage(@RequestParam(value = "message", required = false) String message, 
                                  Model model) {
        if ("logout_success".equals(message)) {
            model.addAttribute("success", "Successfully logged out!");
        }
        return "jwt-login";
    }

    @GetMapping("/jwt-signup")
    public String showJwtSignupPage() {
        return "jwt-signup";
    }

    @PostMapping("/jwt-login")
    public String jwtLogin(@Valid @ModelAttribute AuthRequest request,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        log.info("Login attempt for email: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("ROLE_USER");

            String accessToken = jwtTokenService.generateAccessToken(userDetails.getUsername(), role);
            String refreshToken = jwtTokenService.generateRefreshToken(userDetails.getUsername());

            // Store tokens in session for the redirect
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("refreshToken", refreshToken);
            session.setAttribute("userEmail", userDetails.getUsername());
            session.setAttribute("userRole", role);

            // Also store tokens in flash attributes for immediate access
            redirectAttributes.addFlashAttribute("accessToken", accessToken);
            redirectAttributes.addFlashAttribute("refreshToken", refreshToken);
            redirectAttributes.addFlashAttribute("userEmail", userDetails.getUsername());
            redirectAttributes.addFlashAttribute("userRole", role);

            log.info("User {} logged in successfully with JWT", userDetails.getUsername());
            
            // Force session to be written
            session.setAttribute("loginTime", System.currentTimeMillis());

            // Redirect directly to appropriate dashboard based on role
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/home";
            } else {
                return "redirect:/user-profile";
            }

        } catch (Exception e) {
            log.error("JWT Login failed for user: {}", request.getEmail(), e);
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/jwt-login";
        }
    }

    @PostMapping("/jwt-signup")
    public String jwtSignup(@Valid @ModelAttribute SignupRequest request,
                           RedirectAttributes redirectAttributes) {
        log.info("Signup attempt for email: {}", request.getEmail());
        try {
            com.example.kitchensink.model.AuthResponse authResponse = authService.registerUser(request);

            // Store tokens in flash attributes for the next page
            redirectAttributes.addFlashAttribute("accessToken", authResponse.getAccessToken());
            redirectAttributes.addFlashAttribute("refreshToken", authResponse.getRefreshToken());
            redirectAttributes.addFlashAttribute("userEmail", authResponse.getEmail());
            redirectAttributes.addFlashAttribute("userRole", authResponse.getRole());

            log.info("User {} registered successfully with JWT", request.getEmail());

            // Redirect to login page after successful signup
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login with your credentials.");
            return "redirect:/jwt-login";

        } catch (RuntimeException e) {
            log.error("JWT Signup failed for user: {}", request.getEmail(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/jwt-signup";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/jwt-logout";
    }

    @GetMapping("/jwt-logout")
    public String jwtLogoutGet(HttpServletResponse response) {
        // Set cache control headers to prevent back button navigation
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        return "redirect:/jwt-login";
    }

    @PostMapping("/jwt-logout")
    public String jwtLogout(HttpServletResponse response) {
        // Set cache control headers to prevent back button navigation
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        return "redirect:/jwt-login";
    }
} 