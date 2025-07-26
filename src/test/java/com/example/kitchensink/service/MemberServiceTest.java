package com.example.kitchensink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberMapper memberMapper;

  @InjectMocks
  private MemberService memberService;

  private Member member;
  private MemberEntity memberEntity;

  @BeforeEach
  void setUp() {
    // Initialize mocks
    MockitoAnnotations.openMocks(this);

    // Initialize sample data
    member = new Member();
    member.setId("1");
    member.setName("John Doe");
    member.setEmail("john.doe@example.com");
    member.setPhoneNumber("1234567890");
    member.setRole("USER");

    memberEntity = new MemberEntity();
    memberEntity.setId("1");
    memberEntity.setName("John Doe");
    memberEntity.setEmail("john.doe@example.com");
    memberEntity.setPhoneNumber("1234567890");
    memberEntity.setRole("USER");
  }

  @Test
  void testRegisterMember() {
    // Mock the mapping and repository behavior
    when(memberMapper.memberToMemberEntity(any(Member.class))).thenReturn(memberEntity);
    when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity);

    // Execute the service method
    memberService.registerMember(member);

    // Verify that the repository's save method was called once with the mapped entity
    verify(memberRepository, times(1)).save(memberEntity);
  }

  @Test
  void testGetAllMembers() {
    // Mock the repository and mapper behavior
    when(memberRepository.findAllOrderedByName()).thenReturn(
        Collections.singletonList(memberEntity));
    when(memberMapper.memberEntityListToMemberList(anyList())).thenReturn(
        Collections.singletonList(member));

    // Execute the service method
    List<Member> result = memberService.getAllMembers();

    // Verify the interactions and the result
    verify(memberRepository, times(1)).findAllOrderedByName();
    assertThat(result).isNotNull().hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("John Doe");
  }

  @Test
  void testFindByEmail() {
    // Mock the repository behavior
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberEntity));

    // Execute the service method
    Optional<MemberEntity> result = memberService.findByEmail("john.doe@example.com");

    // Verify the result
    verify(memberRepository, times(1)).findByEmail("john.doe@example.com");
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
  }

  @Test
  void testFindById_WhenMemberExists() {
    // Mock the repository and mapper behavior
    when(memberRepository.findById(anyString())).thenReturn(Optional.of(memberEntity));
    when(memberMapper.memberEntityToMember(any(MemberEntity.class))).thenReturn(member);

    // Execute the service method
    Member result = memberService.findById("1");

    // Verify the interactions and the result
    verify(memberRepository, times(1)).findById("1");
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("1");
    assertThat(result.getName()).isEqualTo("John Doe");
  }

  @Test
  void testFindById_WhenMemberDoesNotExist() {
    // Mock the repository behavior to return an empty optional
    when(memberRepository.findById(anyString())).thenReturn(Optional.empty());

    // Execute the service method
    Member result = memberService.findById("1");

    // Verify the interactions and the result
    verify(memberRepository, times(1)).findById("1");
    assertNull(result); // Assert that the result is null when no member is found
  }
}
