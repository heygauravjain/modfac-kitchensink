package com.example.kitchensink.controller;

import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private final MemberRepository repository;

  private final MemberService memberService;

  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  public RestService(MemberRepository repository,
      MemberService memberService) {
    this.repository = repository;
    this.memberService = memberService;
  }

  // Get a list of all members
  @GetMapping
  public List<Member> listAllMembers() {
    return memberService.getAllMembers();
  }

  // Get member by ID
  @GetMapping("/{id}")
  public ResponseEntity<Member> lookupMemberById(@PathVariable("id") String id) {
    Member member = memberService.findById(id);
    if (Objects.isNull(member)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(member);
  }

  // REST endpoint for creating a new member
  @PostMapping
  public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
    try {
      // Register the new member
      memberService.registerMember(member);

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
