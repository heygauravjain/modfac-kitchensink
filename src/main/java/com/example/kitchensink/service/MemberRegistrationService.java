package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberRegistrationService {

  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  private final MemberRepository memberRepository;

  public MemberRegistrationService(
      MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public void registerMember(Member member) {
    log.info("Registering {}", member);
    MemberEntity memberEntity = memberMapper.memberToMemberEntity(member);
    memberRepository.save(memberEntity);
  }

  public List<Member> getAllMembers() {
    List<MemberEntity> memberEntities = memberRepository.findAllOrderedByName();
    return memberMapper.memberEntityListToMemberList(memberEntities);
  }
}
