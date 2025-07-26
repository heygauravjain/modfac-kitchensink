package com.example.kitchensink.controller;

import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
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
  public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {

    // setting password null right now, But this needs to be updated as per requirements. We can
    // use encoder here as well and take password as input
    member.setPassword(null);
    memberService.registerMember(member);
    return new ResponseEntity<>(member, HttpStatus.CREATED);
  }
}
