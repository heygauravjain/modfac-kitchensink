package com.example.kitchensink.controller;

import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST API controller for member management operations.
 *
 * @author Gaurav Jain
 */
@RestController
@RequestMapping("/admin/members")
@Tag(name = "Member Management", description = "APIs for managing members")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
@RequiredArgsConstructor
public class RestService {

  private final MemberService memberService;

  /** 
   * REST endpoint for listing all members.
   *
   * @return the list of members
   */
  @Operation(summary = "List All Members")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved list of members"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  public ResponseEntity<List<Member>> listAllMembers() {
    List<Member> members = memberService.getAllMembers();
    return ResponseEntity.ok(members);
  }

  /** 
   * REST endpoint for looking up a member by ID.
   * @param id the member ID
   * @return the member details
  */
  @Operation(summary = "Lookup Member by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved member details"),
      @ApiResponse(responseCode = "404", description = "Member not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{id}")
  public ResponseEntity<Member> lookupMemberById(@PathVariable("id") String id) {
    Member member = memberService.findById(id);
    if (member == null) {
      throw new ResourceNotFoundException("Member with ID " + id + " not found.");
    }
    return ResponseEntity.ok(member);
  }

  /** 
   * REST endpoint for looking up a member by email.
   * @param email the member email
   * @return the member details
  */
  @Operation(summary = "Lookup Member by Email")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved member details"),
      @ApiResponse(responseCode = "404", description = "Member not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/search")
  public ResponseEntity<Member> lookupMemberByEmail(@RequestParam("email") String email) {
    Member member = memberService.findMemberByEmail(email);
    if (member == null) {
      throw new ResourceNotFoundException("Member with email " + email + " not found.");
    }
    return ResponseEntity.ok(member);
  }

  /** 
   * REST endpoint for registering a new member.
   * @param member the member details
   * @return the registered member
   */
  @Operation(summary = "Register New Member")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully registered new member"),
      @ApiResponse(responseCode = "400", description = "Invalid member data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  public ResponseEntity<Member> registerMember(@Valid @RequestBody Member member) {
    log.info("Registering new member: {}", member.getEmail());
    log.info("Member details - Name: {}, Email: {}, Role: {}, Phone: {}", 
             member.getName(), member.getEmail(), member.getRole(), member.getPhoneNumber());
    
    try {
      Member registeredMember = memberService.registerMember(member);
      log.info("Member registered successfully: {}", registeredMember.getEmail());
      return new ResponseEntity<>(registeredMember, HttpStatus.CREATED);
    } catch (Exception e) {
      log.error("Error registering member: {}", e.getMessage(), e);
      throw e;
    }
  }

  /** 
   * REST endpoint for deleting a member by ID.
   * @param id the member ID
   * @return success message
   */
  @Operation(summary = "Delete Member by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted member"),
      @ApiResponse(responseCode = "404", description = "Member not found"),
      @ApiResponse(responseCode = "403", description = "Cannot delete your own account"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteMemberById(@PathVariable("id") String id) {
    Member member = memberService.findById(id);
    if (member == null) {
      throw new ResourceNotFoundException("Member with ID " + id + " not found.");
    }
    
    // Check if user is trying to delete their own account
    if (isCurrentUser(member.getEmail())) {
      log.warn("User {} attempted to delete their own account", member.getEmail());
      return new ResponseEntity<>("Cannot delete your own account", HttpStatus.FORBIDDEN);
    }
    
    memberService.deleteById(id);
    return new ResponseEntity<>("Member deleted successfully", HttpStatus.OK);
  }

  /** 
   * REST endpoint for updating a member's details.
   * @param updatedMember the updated member details
   * @param id the member ID
   * @return the updated member
   */
  @Operation(summary = "Update Member Details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated member"),
      @ApiResponse(responseCode = "404", description = "Member not found"),
      @ApiResponse(responseCode = "403", description = "Cannot edit your own account"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{id}")
  public ResponseEntity<Member> updateMember(@Valid @RequestBody Member updatedMember,
      @PathVariable("id") String id) {

    try {
      // Check if the member exists before updating
      Member existingMember = memberService.findById(id);
      if (existingMember == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Member not found
      }
      
      // Check if user is trying to edit their own account
      if (isCurrentUser(existingMember.getEmail())) {
        log.warn("User {} attempted to edit their own account", existingMember.getEmail());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Cannot edit own account
      }
      
      Member savedMember = memberService.updateMember(existingMember, updatedMember);

      // Return the updated member
      return new ResponseEntity<>(savedMember, HttpStatus.OK);
    } catch (Exception e) {
      // Handle exceptions and return appropriate error status
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/auth/login")
  @Operation(summary = "Login to get JWT token", description = "Login with email and password to get JWT token for API testing")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<String> loginForToken(
      @Parameter(description = "User email", required = true) @RequestParam String email,
      @Parameter(description = "User password", required = true) @RequestParam String password) {
    
    try {
      // This is a simple endpoint for Swagger testing
      // In a real application, you would use proper authentication service
      
      // For demo purposes, accept any valid email/password combination
      // In production, validate against the database
      if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
        return ResponseEntity.ok("Login successful. Use the JWT token from your login session for API calls.");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    } catch (Exception e) {
      log.error("Login error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
    }
  }

  /**
   * Helper method to check if the given email belongs to the current authenticated user
   */
  private boolean isCurrentUser(String email) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && email.equals(authentication.getName());
  }
}
