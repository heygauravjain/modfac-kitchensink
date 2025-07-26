package com.example.kitchensink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.entity.MemberDocument;
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
  private MemberDocument memberDocument;

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

    memberDocument = new MemberDocument();
    memberDocument.setId("1");
    memberDocument.setName("John Doe");
    memberDocument.setEmail("john.doe@example.com");
    memberDocument.setPhoneNumber("1234567890");
    memberDocument.setRole("USER");
  }

  // Positive scenario: Registering a valid member
  @Test
  void testRegisterMember() {
    when(memberMapper.memberToMemberEntity(any(Member.class))).thenReturn(memberDocument);
    when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);

    memberService.registerMember(member);

    verify(memberRepository, times(1)).save(memberDocument);
  }

  // Negative scenario: Registering a null member
  @Test
  void testRegisterMember_NullMember() {
    assertThrows(IllegalArgumentException.class, () -> memberService.registerMember(null));
    verify(memberRepository, times(0)).save(any(MemberDocument.class));
  }

  // Negative scenario: Registering a member with missing fields
  @Test
  void testRegisterMember_MemberWithMissingFields() {
    member.setName(null);  // Missing required name field
    when(memberMapper.memberToMemberEntity(any(Member.class))).thenThrow(
        new IllegalArgumentException("Invalid member data"));

    assertThrows(IllegalArgumentException.class, () -> memberService.registerMember(member));
    verify(memberRepository, times(0)).save(any(MemberDocument.class));
  }

  // Positive scenario: Retrieve all members
  @Test
  void testGetAllMembers() {
    when(memberRepository.findAllOrderedByName()).thenReturn(
        Collections.singletonList(memberDocument));
    when(memberMapper.memberEntityListToMemberList(anyList())).thenReturn(
        Collections.singletonList(member));

    List<Member> result = memberService.getAllMembers();

    verify(memberRepository, times(1)).findAllOrderedByName();
    assertThat(result).isNotNull().hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("John Doe");
  }

  // Negative scenario: Empty list when no members are found
  @Test
  void testGetAllMembers_EmptyList() {
    when(memberRepository.findAllOrderedByName()).thenReturn(Collections.emptyList());
    when(memberMapper.memberEntityListToMemberList(anyList())).thenReturn(Collections.emptyList());

    List<Member> result = memberService.getAllMembers();

    verify(memberRepository, times(1)).findAllOrderedByName();
    assertThat(result).isEmpty();  // Should return an empty list
  }

  // Positive scenario: Retrieve a member by email
  @Test
  void testFindByEmail() {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberDocument));

    Optional<MemberDocument> result = memberService.findByEmail("john.doe@example.com");

    verify(memberRepository, times(1)).findByEmail("john.doe@example.com");
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
  }

  // Negative scenario: Member not found by email
  @Test
  void testFindByEmail_MemberNotFound() {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    Optional<MemberDocument> result = memberService.findByEmail("unknown@example.com");

    verify(memberRepository, times(1)).findByEmail("unknown@example.com");
    assertThat(result).isNotPresent();  // No member found
  }

  // Positive scenario: Retrieve a member by ID
  @Test
  void testFindById_WhenMemberExists() {
    when(memberRepository.findById(anyString())).thenReturn(Optional.of(memberDocument));
    when(memberMapper.memberEntityToMember(any(MemberDocument.class))).thenReturn(member);

    Member result = memberService.findById("1");

    verify(memberRepository, times(1)).findById("1");
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("1");
    assertThat(result.getName()).isEqualTo("John Doe");
  }

  // Negative scenario: Member not found by ID
  @Test
  void testFindById_WhenMemberDoesNotExist() {
    when(memberRepository.findById(anyString())).thenReturn(Optional.empty());

    Member result = memberService.findById("1");

    verify(memberRepository, times(1)).findById("1");
    assertNull(result);  // Assert that the result is null when no member is found
  }

  // Edge scenario: Retrieve member by ID with invalid/null input
  @Test
  void testFindById_NullId() {
    assertThrows(IllegalArgumentException.class, () -> memberService.findById(null));
    verify(memberRepository, times(0)).findById(anyString());
  }
}
