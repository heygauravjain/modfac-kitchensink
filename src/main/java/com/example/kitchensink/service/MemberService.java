package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  private final MemberRepository memberRepository;
  
  private final PasswordEncoder passwordEncoder;

  public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Registers a new member by saving the member document in the database.
   */
  public Member registerMember(Member member) {
    MemberDocument memberDocument = memberMapper.memberToMemberEntity(member);
    
    // Normalize role and encode password
    normalizeRole(memberDocument);
    encodePasswordIfPresent(memberDocument);
    
    memberRepository.save(memberDocument);

    return memberMapper.memberEntityToMember(memberDocument);
  }

  public Member updateMember(Member existingMember, Member updatedMember) {
    // Get the existing member from database to preserve the encrypted password
    MemberDocument existingDocument = memberRepository.findById(existingMember.getId())
        .orElseThrow(() -> new RuntimeException("Member not found with ID: " + existingMember.getId()));
    
    // Update the existing member's details with new values
    existingMember.setName(updatedMember.getName());
    existingMember.setEmail(updatedMember.getEmail());
    existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
    
    // Normalize role
    String role = normalizeRole(updatedMember.getRole());
    existingMember.setRole(role);
    
    // Handle password update
    handlePasswordUpdate(existingMember, updatedMember, existingDocument);

    MemberDocument memberDocument = memberMapper.memberToMemberEntity(existingMember);
    
    // Only encrypt the password if it was updated (new password provided)
    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
      encodePasswordIfPresent(memberDocument);
    } else {
      // Preserve the existing encrypted password
      memberDocument.setPassword(existingDocument.getPassword());
    }
    
    memberRepository.save(memberDocument);

    return memberMapper.memberEntityToMember(memberDocument);
  }

  /**
   * Retrieves all members from the database, ordered by name.
   */
  public List<Member> getAllMembers() {
    List<MemberDocument> memberDocuments = memberRepository.findAllOrderedByName();
    return memberMapper.memberEntityListToMemberList(memberDocuments);
  }

  /**
   * Finds a member by their email.
   *
   * @param email the email of the member to search for.
   * @return an Optional containing the found member or an empty Optional if no member is found.
   */
  public Optional<MemberDocument> findByEmail(String email) {
    return memberRepository.findByEmail(email);
  }

  /**
   * Finds a member by their email and returns the Member model.
   *
   * @param email the email of the member to search for.
   * @return the found member, or null if no member is found.
   */
  public Member findMemberByEmail(String email) {
    Optional<MemberDocument> memberDocument = memberRepository.findByEmail(email);
    return memberDocument.map(memberMapper::memberEntityToMember).orElse(null);
  }

  /**
   * Finds a member by their ID.
   *
   * @param id the ID of the member to search for.
   * @return the found member, or null if no member is found.
   */
  public Member findById(String id) {
    MemberDocument memberDocument = memberRepository.findById(id).orElse(null);
    return memberDocument != null ? memberMapper.memberEntityToMember(memberDocument) : null;
  }

  public void deleteById(String id) {
    if (!StringUtils.hasText(id)) {
      log.error("Attempted to find a member with a null or empty ID.");
      throw new IllegalArgumentException("ID cannot be null or empty.");
    }
    memberRepository.deleteById(id);
  }

  /**
   * Helper method to normalize role (ensure ROLE_ prefix)
   */
  private void normalizeRole(MemberDocument memberDocument) {
    String role = memberDocument.getRole();
    if (role != null && !role.startsWith("ROLE_")) {
      role = "ROLE_" + role.toUpperCase();
      memberDocument.setRole(role);
    }
  }

  /**
   * Helper method to normalize role (ensure ROLE_ prefix)
   */
  private String normalizeRole(String role) {
    if (role != null && !role.startsWith("ROLE_")) {
      return "ROLE_" + role.toUpperCase();
    }
    return role;
  }

  /**
   * Helper method to encode password if present
   */
  private void encodePasswordIfPresent(MemberDocument memberDocument) {
    if (memberDocument.getPassword() != null && !memberDocument.getPassword().isEmpty()) {
      String encryptedPassword = passwordEncoder.encode(memberDocument.getPassword());
      memberDocument.setPassword(encryptedPassword);
    }
  }

  /**
   * Helper method to handle password update logic
   */
  private void handlePasswordUpdate(Member existingMember, Member updatedMember, MemberDocument existingDocument) {
    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
      existingMember.setPassword(updatedMember.getPassword());
    } else {
      // Keep the existing encrypted password from database
      existingMember.setPassword(existingDocument.getPassword());
    }
  }
}
