package com.example.kitchensink.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.kitchensink.controller.RestService;
import com.example.kitchensink.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {RestService.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberService memberService;

  @BeforeEach
  void setUp() {
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
    // Simulate an ADMIN user
  void testHandleResourceNotFoundException() throws Exception {
    // Arrange: Mock the service to throw ResourceNotFoundException
    when(memberService.findById("123")).thenThrow(
        new ResourceNotFoundException("Member with ID 123 not found."));

    // Act & Assert: Make a GET request to a member endpoint that triggers ResourceNotFoundException
    mockMvc.perform(get("/rest/members/123"))
        .andExpect(status().isNotFound()) // Expect 404 status
        .andExpect(jsonPath("$.message").value("Member with ID 123 not found."))
        .andExpect(jsonPath("$.details").exists())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
    // Simulate an ADMIN user
  void testHandleAllExceptions() throws Exception {
    // Arrange: Mock the service to throw a general exception
    when(memberService.getAllMembers()).thenThrow(new RuntimeException("Unexpected error"));

    // Act & Assert: Make a GET request that triggers a general exception
    mockMvc.perform(get("/rest/members"))
        .andExpect(status().isInternalServerError()) // Expect 500 status
        .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
        .andExpect(jsonPath("$.details").value("Unexpected error"))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
