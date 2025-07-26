package com.example.kitchensink.controller;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberRegistrationService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/members")
public class RestService {

  private final MemberRepository repository;

  private final MemberRegistrationService memberRegistrationService;

  public RestService(MemberRepository repository,
      MemberRegistrationService memberRegistrationService) {
    this.repository = repository;
    this.memberRegistrationService = memberRegistrationService;
  }

  // Get a list of all members
  @GetMapping
  public List<Member> listAllMembers() {
    return memberRegistrationService.getAllMembers();
  }

  // Get member by ID
  @GetMapping("/{id}")
  public ResponseEntity<MemberEntity> lookupMemberById(@PathVariable("id") String id) {
    MemberEntity memberEntity = repository.findById(id).orElse(null);
    if (memberEntity == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(memberEntity);
  }

  // REST endpoint for creating a new member
  @PostMapping
  public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
    try {
      // Register the new member
      memberRegistrationService.registerMember(member);

      // Return success response
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      // Handle generic exceptions
      Map<String, String> responseObj = new HashMap<>();
      responseObj.put("error", e.getMessage());
      return new ResponseEntity<>(responseObj, HttpStatus.BAD_REQUEST);
    }
  }
}
