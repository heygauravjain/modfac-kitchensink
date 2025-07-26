package com.example.kitchensink.controller;

import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class MemberController.
 *
 * @author Gaurav Jain
 */
@RestController
@RequestMapping("/admin/members")
@Slf4j
public class RestService {

  private final MemberService memberService;

  public RestService(MemberService memberService) {
    this.memberService = memberService;
  }

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
    if (Objects.isNull(member)) {
      throw new ResourceNotFoundException("Member with ID " + id + " not found.");
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

    // setting password null right now, But this needs to be updated as per requirements. We can
    // use encoder here as well if we want to take password as input
    member.setPassword(null);
    Member registeredMember = memberService.registerMember(member);
    return new ResponseEntity<>(registeredMember, HttpStatus.CREATED);
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
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteMemberById(@PathVariable("id") String id) {
    Member member = memberService.findById(id);
    if (Objects.isNull(member)) {
      throw new ResourceNotFoundException("Member with ID " + id + " not found.");
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
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{id}")
  public ResponseEntity<Member> updateMember(@Valid @RequestBody Member updatedMember,
      @PathVariable("id") String id) {

    log.info("Updating Member with Id {}", id);
    try {
      // Check if the member exists before updating
      Member existingMember = memberService.findById(id);
      if (Objects.isNull(existingMember)) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Member not found
      }
      Member savedMember = memberService.updateMember(existingMember, updatedMember);

      // Return the updated member
      return new ResponseEntity<>(savedMember, HttpStatus.OK);
    } catch (Exception e) {
      // Handle exceptions and return appropriate error status
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
