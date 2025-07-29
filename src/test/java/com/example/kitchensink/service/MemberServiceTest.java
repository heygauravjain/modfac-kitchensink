package com.example.kitchensink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberMapper memberMapper;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberService memberService;

  private Member member;
  private MemberDocument memberDocument;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Mock password encoder
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    // Initialize sample data
    member = new Member();
    member.setId("1");
    member.setName("John Doe");
    member.setEmail("john.doe@example.com");
    member.setPassword("testPassword123"); // Add password for testing
    member.setPhoneNumber("1234567890");
    member.setRole("USER");

    memberDocument = new MemberDocument();
    memberDocument.setId("1");
    memberDocument.setName("John Doe");
    memberDocument.setEmail("john.doe@example.com");
    memberDocument.setPassword("encodedPassword"); // Set encoded password
    memberDocument.setPhoneNumber("1234567890");
    memberDocument.setRole("USER");
  }

  // Positive scenario: Registering a valid member
  @Test
  void testRegisterMember() {
    when(memberMapper.memberToMemberEntity(any(Member.class))).thenReturn(memberDocument);
    when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
    when(memberMapper.memberEntityToMember(any(MemberDocument.class))).thenReturn(member);

    Member result = memberService.registerMember(member);

    verify(memberRepository, times(1)).save(memberDocument);
    verify(passwordEncoder, times(1)).encode(anyString());
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(member.getEmail());
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
    assertThat(result.get(0).getName()).isEqualTo("John Doe");
  }

  // Negative scenario: Empty list when no members are found
  @Test
  void testGetAllMembers_EmptyList() {
    when(memberRepository.findAllOrderedByName()).thenReturn(Collections.emptyList());
    when(memberMapper.memberEntityListToMemberList(anyList())).thenReturn(Collections.emptyList());

    List<Member> result = memberService.getAllMembers();

    verify(memberRepository, times(1)).findAllOrderedByName();
    assertThat(result).isEmpty();
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
    assertThat(result).isNotPresent();
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
    assertNull(result);
  }

  // Positive scenario: Successfully update a member
  @Test
  void testUpdateMember_Success() {
    Member updatedMember = new Member("1", "Jane Doe", "jane.doe@example.com", "1234567890", null,
        "ADMIN");

    when(memberMapper.memberToMemberEntity(any(Member.class))).thenReturn(memberDocument);
    when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
    when(memberMapper.memberEntityToMember(any(MemberDocument.class))).thenReturn(updatedMember);

    Member result = memberService.updateMember(member, updatedMember);

    //verify(memberRepository, times(1)).save(memberDocument);
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Jane Doe");
    assertThat(result.getEmail()).isEqualTo("jane.doe@example.com");
  }

  // Positive scenario: Successfully delete a member by ID
  @Test
  void testDeleteById_Success() {
    when(memberRepository.findById("1")).thenReturn(Optional.of(memberDocument));
    doNothing().when(memberRepository).deleteById("1");

    memberService.deleteById("1");

    verify(memberRepository, times(1)).deleteById("1");
  }

  // Negative scenario: Delete member with null or empty ID
  @Test
  void testDeleteById_NullOrEmptyId() {
    assertThrows(IllegalArgumentException.class, () -> memberService.deleteById(""));
    verify(memberRepository, times(0)).deleteById(anyString());
  }
}
