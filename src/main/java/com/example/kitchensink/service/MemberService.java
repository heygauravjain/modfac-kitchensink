package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  private final MemberRepository memberRepository;

  public MemberService(
      MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public void registerMember(Member member) {
    log.info("Registering {}", member);
    MemberDocument memberDocument = memberMapper.memberToMemberEntity(member);
    memberRepository.save(memberDocument);
  }

  public List<Member> getAllMembers() {
    List<MemberDocument> memberEntities = memberRepository.findAllOrderedByName();
    return memberMapper.memberEntityListToMemberList(memberEntities);
  }

  public Optional<MemberDocument> findByEmail(String email) {
    return memberRepository.findByEmail(email);
  }

  public Member findById(String id) {
    MemberDocument memberDocument = memberRepository.findById(id).orElse(null);
    if (memberDocument != null) {
      return memberMapper.memberEntityToMember(memberDocument);
    } else {
      return null;
    }
  }
}
