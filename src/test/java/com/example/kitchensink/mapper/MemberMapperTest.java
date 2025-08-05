package com.example.kitchensink.mapper;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.model.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class for MemberMapper.
 * Tests mapping between Member model and MemberDocument entity.
 */
class MemberMapperTest {

    private MemberMapper memberMapper;
    private Member member;
    private MemberDocument memberDocument;

    @BeforeEach
    void setUp() {
        memberMapper = MemberMapper.INSTANCE;
        
        member = new Member();
        member.setId("1");
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPassword("password123");
        member.setPhoneNumber("1234567890");
        member.setRole("USER");

        memberDocument = new MemberDocument();
        memberDocument.setId("1");
        memberDocument.setName("John Doe");
        memberDocument.setEmail("john@example.com");
        memberDocument.setPassword("password123");
        memberDocument.setPhoneNumber("1234567890");
        memberDocument.setRole("USER");
    }

    @Test
    void testMemberToMemberEntity() {
        // When
        MemberDocument result = memberMapper.memberToMemberEntity(member);

        // Then
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        assertEquals(member.getName(), result.getName());
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getPassword(), result.getPassword());
        assertEquals(member.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(member.getRole(), result.getRole());
    }

    @Test
    void testMemberEntityToMember() {
        // When
        Member result = memberMapper.memberEntityToMember(memberDocument);

        // Then
        assertNotNull(result);
        assertEquals(memberDocument.getId(), result.getId());
        assertEquals(memberDocument.getName(), result.getName());
        assertEquals(memberDocument.getEmail(), result.getEmail());
        assertNull(result.getPassword()); // Password should be ignored
        assertEquals(memberDocument.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(memberDocument.getRole(), result.getRole());
    }

    @Test
    void testMemberEntityListToMemberList() {
        // Given
        List<MemberDocument> memberDocuments = Arrays.asList(memberDocument);

        // When
        List<Member> result = memberMapper.memberEntityListToMemberList(memberDocuments);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(memberDocument.getId(), result.get(0).getId());
        assertEquals(memberDocument.getName(), result.get(0).getName());
        assertEquals(memberDocument.getEmail(), result.get(0).getEmail());
        assertEquals(memberDocument.getPassword(), result.get(0).getPassword());
        assertEquals(memberDocument.getPhoneNumber(), result.get(0).getPhoneNumber());
        assertEquals(memberDocument.getRole(), result.get(0).getRole());
    }

    @Test
    void testMemberToMemberEntityWithNullValues() {
        // Given
        Member memberWithNulls = new Member();
        memberWithNulls.setId(null);
        memberWithNulls.setName(null);
        memberWithNulls.setEmail(null);
        memberWithNulls.setPassword(null);
        memberWithNulls.setPhoneNumber(null);
        memberWithNulls.setRole(null);

        // When
        MemberDocument result = memberMapper.memberToMemberEntity(memberWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getPassword());
        assertNull(result.getPhoneNumber());
        assertNull(result.getRole());
    }

    @Test
    void testMemberEntityToMemberWithNullValues() {
        // Given
        MemberDocument memberDocumentWithNulls = new MemberDocument();
        memberDocumentWithNulls.setId(null);
        memberDocumentWithNulls.setName(null);
        memberDocumentWithNulls.setEmail(null);
        memberDocumentWithNulls.setPassword(null);
        memberDocumentWithNulls.setPhoneNumber(null);
        memberDocumentWithNulls.setRole(null);

        // When
        Member result = memberMapper.memberEntityToMember(memberDocumentWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getPassword()); // Should be null due to @Mapping(ignore = true)
        assertNull(result.getPhoneNumber());
        assertNull(result.getRole());
    }

    @Test
    void testMemberEntityListToMemberListWithNullValues() {
        // Given
        MemberDocument memberDocumentWithNulls = new MemberDocument();
        memberDocumentWithNulls.setId(null);
        memberDocumentWithNulls.setName(null);
        memberDocumentWithNulls.setEmail(null);
        memberDocumentWithNulls.setPassword(null);
        memberDocumentWithNulls.setPhoneNumber(null);
        memberDocumentWithNulls.setRole(null);

        List<MemberDocument> memberDocuments = Arrays.asList(memberDocumentWithNulls);

        // When
        List<Member> result = memberMapper.memberEntityListToMemberList(memberDocuments);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getId());
        assertNull(result.get(0).getName());
        assertNull(result.get(0).getEmail());
        assertNull(result.get(0).getPassword());
        assertNull(result.get(0).getPhoneNumber());
        assertNull(result.get(0).getRole());
    }

    @Test
    void testMemberEntityListToMemberListWithEmptyList() {
        // Given
        List<MemberDocument> emptyList = new ArrayList<>();

        // When
        List<Member> result = memberMapper.memberEntityListToMemberList(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMemberEntityListToMemberListWithMultipleItems() {
        // Given
        MemberDocument memberDocument2 = new MemberDocument();
        memberDocument2.setId("2");
        memberDocument2.setName("Jane Doe");
        memberDocument2.setEmail("jane@example.com");
        memberDocument2.setPassword("password456");
        memberDocument2.setPhoneNumber("9876543210");
        memberDocument2.setRole("ADMIN");

        List<MemberDocument> memberDocuments = Arrays.asList(memberDocument, memberDocument2);

        // When
        List<Member> result = memberMapper.memberEntityListToMemberList(memberDocuments);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Check first item
        assertEquals(memberDocument.getId(), result.get(0).getId());
        assertEquals(memberDocument.getName(), result.get(0).getName());
        assertEquals(memberDocument.getEmail(), result.get(0).getEmail());
        assertEquals(memberDocument.getPassword(), result.get(0).getPassword());
        assertEquals(memberDocument.getPhoneNumber(), result.get(0).getPhoneNumber());
        assertEquals(memberDocument.getRole(), result.get(0).getRole());
        
        // Check second item
        assertEquals(memberDocument2.getId(), result.get(1).getId());
        assertEquals(memberDocument2.getName(), result.get(1).getName());
        assertEquals(memberDocument2.getEmail(), result.get(1).getEmail());
        assertEquals(memberDocument2.getPassword(), result.get(1).getPassword());
        assertEquals(memberDocument2.getPhoneNumber(), result.get(1).getPhoneNumber());
        assertEquals(memberDocument2.getRole(), result.get(1).getRole());
    }

    @Test
    void testMemberToMemberEntityWithEmptyStrings() {
        // Given
        Member memberWithEmptyStrings = new Member();
        memberWithEmptyStrings.setId("");
        memberWithEmptyStrings.setName("");
        memberWithEmptyStrings.setEmail("");
        memberWithEmptyStrings.setPassword("");
        memberWithEmptyStrings.setPhoneNumber("");
        memberWithEmptyStrings.setRole("");

        // When
        MemberDocument result = memberMapper.memberToMemberEntity(memberWithEmptyStrings);

        // Then
        assertNotNull(result);
        assertEquals("", result.getId());
        assertEquals("", result.getName());
        assertEquals("", result.getEmail());
        assertEquals("", result.getPassword());
        assertEquals("", result.getPhoneNumber());
        assertEquals("", result.getRole());
    }

    @Test
    void testMemberEntityToMemberWithEmptyStrings() {
        // Given
        MemberDocument memberDocumentWithEmptyStrings = new MemberDocument();
        memberDocumentWithEmptyStrings.setId("");
        memberDocumentWithEmptyStrings.setName("");
        memberDocumentWithEmptyStrings.setEmail("");
        memberDocumentWithEmptyStrings.setPassword("");
        memberDocumentWithEmptyStrings.setPhoneNumber("");
        memberDocumentWithEmptyStrings.setRole("");

        // When
        Member result = memberMapper.memberEntityToMember(memberDocumentWithEmptyStrings);

        // Then
        assertNotNull(result);
        assertEquals("", result.getId());
        assertEquals("", result.getName());
        assertEquals("", result.getEmail());
        assertNull(result.getPassword()); // Should be null due to @Mapping(ignore = true)
        assertEquals("", result.getPhoneNumber());
        assertEquals("", result.getRole());
    }

    @Test
    void testMemberToMemberEntityWithSpecialCharacters() {
        // Given
        Member memberWithSpecialChars = new Member();
        memberWithSpecialChars.setId("1");
        memberWithSpecialChars.setName("José María O'Connor");
        memberWithSpecialChars.setEmail("test+tag@example.co.uk");
        memberWithSpecialChars.setPassword("Complex1@Pass");
        memberWithSpecialChars.setPhoneNumber("1234567890");
        memberWithSpecialChars.setRole("USER");

        // When
        MemberDocument result = memberMapper.memberToMemberEntity(memberWithSpecialChars);

        // Then
        assertNotNull(result);
        assertEquals("José María O'Connor", result.getName());
        assertEquals("test+tag@example.co.uk", result.getEmail());
        assertEquals("Complex1@Pass", result.getPassword());
    }

    @Test
    void testMemberEntityToMemberWithSpecialCharacters() {
        // Given
        MemberDocument memberDocumentWithSpecialChars = new MemberDocument();
        memberDocumentWithSpecialChars.setId("1");
        memberDocumentWithSpecialChars.setName("José María O'Connor");
        memberDocumentWithSpecialChars.setEmail("test+tag@example.co.uk");
        memberDocumentWithSpecialChars.setPassword("Complex1@Pass");
        memberDocumentWithSpecialChars.setPhoneNumber("1234567890");
        memberDocumentWithSpecialChars.setRole("USER");

        // When
        Member result = memberMapper.memberEntityToMember(memberDocumentWithSpecialChars);

        // Then
        assertNotNull(result);
        assertEquals("José María O'Connor", result.getName());
        assertEquals("test+tag@example.co.uk", result.getEmail());
        assertNull(result.getPassword()); // Should be null due to @Mapping(ignore = true)
    }
} 