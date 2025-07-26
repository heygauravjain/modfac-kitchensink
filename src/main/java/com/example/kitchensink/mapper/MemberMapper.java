package com.example.kitchensink.mapper;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.model.Member;
import java.util.List;


@Mapper
public interface MemberMapper {

  MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

  MemberEntity memberToMemberEntity(Member member);

  List<Member> memberEntityListToMemberList(List<MemberEntity> memberEntities);

  Member memberEntityToMember(MemberEntity memberEntity);
}
