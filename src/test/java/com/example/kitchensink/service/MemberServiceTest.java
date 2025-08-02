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
    member.setPassword("testPassword123");
    member.setPhoneNumber("1234567890");
    member.setRole("USER");

    memberDocument = new MemberDocument();
    memberDocument.setId("1");
    memberDocument.setName("John Doe");
    memberDocument.setEmail("john.doe@example.com");
    memberDocument.setPassword("encodedPassword");
    memberDocument.setPhoneNumber("1234567890");
    memberDocument.setRole("ROLE_USER");
  }

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

  @Test
  void testGetAllMembers_EmptyList() {
    when(memberRepository.findAllOrderedByName()).thenReturn(Collections.emptyList());
    when(memberMapper.memberEntityListToMemberList(anyList())).thenReturn(Collections.emptyList());

    List<Member> result = memberService.getAllMembers();

    verify(memberRepository, times(1)).findAllOrderedByName();
    assertThat(result).isEmpty();
  }

  @Test
  void testFindByEmail() {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberDocument));

    Optional<MemberDocument> result = memberService.findByEmail("john.doe@example.com");

    verify(memberRepository, times(1)).findByEmail("john.doe@example.com");
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
  }

  @Test
  void testFindByEmail_MemberNotFound() {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    Optional<MemberDocument> result = memberService.findByEmail("unknown@example.com");

    verify(memberRepository, times(1)).findByEmail("unknown@example.com");
    assertThat(result).isNotPresent();
  }

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

  @Test
  void testFindById_WhenMemberDoesNotExist() {
    when(memberRepository.findById(anyString())).thenReturn(Optional.empty());

    Member result = memberService.findById("1");

    verify(memberRepository, times(1)).findById("1");
    assertNull(result);
  }

  @Test
  void testUpdateMember_Success() {
    // Create the existing member that should be found in the database
    Member existingMember = new Member("1", "John Doe", "john.doe@example.com", "1234567890", null, "USER");
    
    // Create the updated member with new details
    Member updatedMember = new Member("1", "Jane Doe", "jane.doe@example.com", "0987654321", null, "ADMIN");
    
    // Create the existing document that should be returned from database
    MemberDocument existingDocument = new MemberDocument();
    existingDocument.setId("1");
    existingDocument.setName("John Doe");
    existingDocument.setEmail("john.doe@example.com");
    existingDocument.setPassword("encodedPassword");
    existingDocument.setPhoneNumber("1234567890");
    existingDocument.setRole("ROLE_USER");
    
    // Create the updated document that should be saved
    MemberDocument updatedDocument = new MemberDocument();
    updatedDocument.setId("1");
    updatedDocument.setName("Jane Doe");
    updatedDocument.setEmail("jane.doe@example.com");
    updatedDocument.setPassword("encodedPassword");
    updatedDocument.setPhoneNumber("0987654321");
    updatedDocument.setRole("ROLE_ADMIN");

    // Mock the repository to return the existing member when findById is called
    when(memberRepository.findById("1")).thenReturn(Optional.of(existingDocument));
    
    // Mock the mapper to return the updated document when converting
    when(memberMapper.memberToMemberEntity(any(Member.class))).thenReturn(updatedDocument);
    
    // Mock the repository to return the updated document when saving
    when(memberRepository.save(any(MemberDocument.class))).thenReturn(updatedDocument);
    
    // Mock the mapper to return the updated member when converting back
    when(memberMapper.memberEntityToMember(any(MemberDocument.class))).thenReturn(updatedMember);

    Member result = memberService.updateMember(existingMember, updatedMember);

    verify(memberRepository, times(1)).findById("1");
    verify(memberRepository, times(1)).save(any(MemberDocument.class));
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Jane Doe");
    assertThat(result.getEmail()).isEqualTo("jane.doe@example.com");
    assertThat(result.getRole()).isEqualTo("ROLE_ADMIN");
  }

  @Test
  void testUpdateMember_MemberNotFound() {
    Member existingMember = new Member("1", "John Doe", "john.doe@example.com", "1234567890", null, "USER");
    Member updatedMember = new Member("1", "Jane Doe", "jane.doe@example.com", "0987654321", null, "ADMIN");

    // Mock the repository to return empty when findById is called
    when(memberRepository.findById("1")).thenReturn(Optional.empty());

    // Test that RuntimeException is thrown
    assertThrows(RuntimeException.class, () -> {
      memberService.updateMember(existingMember, updatedMember);
    });

    verify(memberRepository, times(1)).findById("1");
    verify(memberRepository, times(0)).save(any(MemberDocument.class));
  }

  @Test
  void testDeleteById_Success() {
    when(memberRepository.findById("1")).thenReturn(Optional.of(memberDocument));
    doNothing().when(memberRepository).deleteById("1");

    memberService.deleteById("1");

    verify(memberRepository, times(1)).deleteById("1");
  }

  @Test
  void testDeleteById_NullOrEmptyId() {
    assertThrows(IllegalArgumentException.class, () -> memberService.deleteById(""));
    verify(memberRepository, times(0)).deleteById(anyString());
  }

  @Test
  void testDeleteById_NullId() {
    assertThrows(IllegalArgumentException.class, () -> memberService.deleteById(null));
    verify(memberRepository, times(0)).deleteById(anyString());
  }
}
