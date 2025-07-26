package com.example.kitchensink.mapper;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.model.Member;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {

  MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

  MemberEntity memberToMemberEntity(Member member);

  List<Member> memberEntityListToMemberList(List<MemberEntity> memberEntities);

  // Map single MemberEntity to Member with password set to null
  @Mapping(target = "password", ignore = true)
  Member memberEntityToMember(MemberEntity memberEntity);

}
