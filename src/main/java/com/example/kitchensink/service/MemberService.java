package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  private final MemberRepository memberRepository;

  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  /**
   * Registers a new member by saving the member document in the database.
   * Throws IllegalArgumentException if the member is null or invalid.
   */
  public void registerMember(Member member) {
    if (member == null) {
      log.error("Attempted to register a null member.");
      throw new IllegalArgumentException("Member cannot be null.");
    }

    // Check for missing required fields
    if (!StringUtils.hasText(member.getName()) || !StringUtils.hasText(member.getEmail())) {
      log.error("Attempted to register a member with missing required fields: {}", member);
      throw new IllegalArgumentException("Member name and email cannot be empty.");
    }

    log.info("Registering {}", member);
    MemberDocument memberDocument = memberMapper.memberToMemberEntity(member);
    memberRepository.save(memberDocument);
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
   * @throws IllegalArgumentException if the provided email is null or empty.
   */
  public Optional<MemberDocument> findByEmail(String email) {
    if (!StringUtils.hasText(email)) {
      log.error("Attempted to find a member with a null or empty email.");
      throw new IllegalArgumentException("Email cannot be null or empty.");
    }

    return memberRepository.findByEmail(email);
  }

  /**
   * Finds a member by their ID.
   *
   * @param id the ID of the member to search for.
   * @return the found member, or null if no member is found.
   * @throws IllegalArgumentException if the provided ID is null or empty.
   */
  public Member findById(String id) {
    if (!StringUtils.hasText(id)) {
      log.error("Attempted to find a member with a null or empty ID.");
      throw new IllegalArgumentException("ID cannot be null or empty.");
    }

    MemberDocument memberDocument = memberRepository.findById(id).orElse(null);
    return memberDocument != null ? memberMapper.memberEntityToMember(memberDocument) : null;
  }
}
