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
    
    // Ensure role has ROLE_ prefix for Spring Security compatibility
    String role = memberDocument.getRole();
    if (role != null && !role.startsWith("ROLE_")) {
      role = "ROLE_" + role.toUpperCase();
      memberDocument.setRole(role);
    }
    
    // Encrypt the password before saving
    if (memberDocument.getPassword() != null && !memberDocument.getPassword().isEmpty()) {
      String encryptedPassword = passwordEncoder.encode(memberDocument.getPassword());
      memberDocument.setPassword(encryptedPassword);
    }
    
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
    
    // Ensure role has ROLE_ prefix for Spring Security compatibility
    String role = updatedMember.getRole();
    if (role != null && !role.startsWith("ROLE_")) {
      role = "ROLE_" + role.toUpperCase();
    }
    existingMember.setRole(role);
    
    // Preserve the existing encrypted password from database
    // Only update password if a new password is explicitly provided
    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
      existingMember.setPassword(updatedMember.getPassword());
    } else {
      // Keep the existing encrypted password from database
      existingMember.setPassword(existingDocument.getPassword());
    }

    MemberDocument memberDocument = memberMapper.memberToMemberEntity(existingMember);
    
    // Only encrypt the password if it was updated (new password provided)
    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
      String encryptedPassword = passwordEncoder.encode(memberDocument.getPassword());
      memberDocument.setPassword(encryptedPassword);
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
}
