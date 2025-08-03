package com.example.kitchensink.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ErrorController.class)
class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.kitchensink.security.JwtTokenService jwtTokenService;

    @MockBean
    private com.example.kitchensink.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private com.example.kitchensink.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void test500ErrorPage() throws Exception {
        mockMvc.perform(get("/error/500"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test500ErrorPageWithParameters() throws Exception {
        mockMvc.perform(get("/error/500")
                .param("error", "Test Error")
                .param("message", "Test Message")
                .param("timestamp", "2025-08-03T01:38:45.704953582"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test404ErrorPage() throws Exception {
        mockMvc.perform(get("/error/404"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGenericErrorPage() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isUnauthorized());
    }
} 