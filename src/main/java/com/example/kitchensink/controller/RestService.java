package com.example.kitchensink.controller;

import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
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

@RestController
@RequestMapping("/admin/members")
@Slf4j
public class RestService {

  private final MemberService memberService;

  public RestService(MemberService memberService) {
    this.memberService = memberService;
  }

  // Get a list of all members
  @GetMapping
  public ResponseEntity<List<Member>> listAllMembers() {
    List<Member> members = memberService.getAllMembers();
    return ResponseEntity.ok(members);
  }

  // Get member by ID
  @GetMapping("/{id}")
  public ResponseEntity<Member> lookupMemberById(@PathVariable("id") String id) {
    Member member = memberService.findById(id);
    if (Objects.isNull(member)) {
      throw new ResourceNotFoundException("Member with ID " + id + " not found.");
    }
    return ResponseEntity.ok(member);
  }

  // REST endpoint for creating a new member
  @PostMapping
  public ResponseEntity<Member> registerMember(@Valid @RequestBody Member member) {

    // setting password null right now, But this needs to be updated as per requirements. We can
    // use encoder here as well if we want to take password as input
    member.setPassword(null);
    Member registeredMember = memberService.registerMember(member);
    return new ResponseEntity<>(registeredMember, HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteMemberById(@PathVariable("id") String id) {
    Member member = memberService.findById(id);
    if (Objects.isNull(member)) {
      throw new ResourceNotFoundException("Member with ID " + id + " not found.");
    }
    memberService.deleteById(id);
    return new ResponseEntity<>("Member deleted successfully", HttpStatus.OK);
  }

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
