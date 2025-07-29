package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  private final MemberRepository memberRepository;
  
  private final BCryptPasswordEncoder passwordEncoder;

  public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Registers a new member by saving the member document in the database.
   */
  public Member registerMember(Member member) {
    log.info("Registering member: {}", member);
    MemberDocument memberDocument = memberMapper.memberToMemberEntity(member);
    
    // Encrypt the password before saving
    if (memberDocument.getPassword() != null && !memberDocument.getPassword().isEmpty()) {
      String encryptedPassword = passwordEncoder.encode(memberDocument.getPassword());
      memberDocument.setPassword(encryptedPassword);
    }
    
    memberRepository.save(memberDocument);

    return memberMapper.memberEntityToMember(memberDocument);
  }

  public Member updateMember(Member existingMember, Member updatedMember) {
    log.info("Updating member: {}", updatedMember);

    // Update the existing member's details with new values
    existingMember.setName(updatedMember.getName());
    existingMember.setEmail(updatedMember.getEmail());
    existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
    existingMember.setRole(updatedMember.getRole());
    
    // Only update password if a new password is explicitly provided
    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
      existingMember.setPassword(updatedMember.getPassword());
    }

    MemberDocument memberDocument = memberMapper.memberToMemberEntity(existingMember);
    
    // Only encrypt the password if it was updated (new password provided)
    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
      String encryptedPassword = passwordEncoder.encode(memberDocument.getPassword());
      memberDocument.setPassword(encryptedPassword);
    }
    // If no new password was provided, the existing encrypted password remains unchanged
    
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
