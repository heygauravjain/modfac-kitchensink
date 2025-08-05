package com.example.kitchensink.repository;

import com.example.kitchensink.entity.MemberDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MemberRepository.
 * Tests MongoDB repository operations and custom queries.
 */
@DataMongoTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MemberDocument testMember;

    @BeforeEach
    void setUp() {
        // Clear the collection before each test
        mongoTemplate.dropCollection(MemberDocument.class);
        
        // Create a test member
        testMember = new MemberDocument();
        testMember.setId("1");
        testMember.setName("John Doe");
        testMember.setEmail("john@example.com");
        testMember.setPassword("encoded_password");
        testMember.setPhoneNumber("1234567890");
        testMember.setRole("USER");
    }

    @Test
    void testSaveMember() {
        // When
        MemberDocument savedMember = memberRepository.save(testMember);

        // Then
        assertNotNull(savedMember);
        assertEquals("1", savedMember.getId());
        assertEquals("John Doe", savedMember.getName());
        assertEquals("john@example.com", savedMember.getEmail());
        assertEquals("encoded_password", savedMember.getPassword());
        assertEquals("1234567890", savedMember.getPhoneNumber());
        assertEquals("USER", savedMember.getRole());
    }

    @Test
    void testFindByEmail_ExistingMember() {
        // Given
        memberRepository.save(testMember);

        // When
        Optional<MemberDocument> foundMember = memberRepository.findByEmail("john@example.com");

        // Then
        assertTrue(foundMember.isPresent());
        assertEquals("john@example.com", foundMember.get().getEmail());
        assertEquals("John Doe", foundMember.get().getName());
    }

    @Test
    void testFindByEmail_NonExistingMember() {
        // When
        Optional<MemberDocument> foundMember = memberRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundMember.isPresent());
    }

    @Test
    void testFindByEmail_NullEmail() {
        // When
        Optional<MemberDocument> foundMember = memberRepository.findByEmail(null);

        // Then
        assertFalse(foundMember.isPresent());
    }

    @Test
    void testFindByEmail_EmptyEmail() {
        // When
        Optional<MemberDocument> foundMember = memberRepository.findByEmail("");

        // Then
        assertFalse(foundMember.isPresent());
    }

    @Test
    void testFindAll() {
        // Given
        MemberDocument member1 = new MemberDocument("1", "John Doe", "john@example.com", "password1", "1234567890", "USER");
        MemberDocument member2 = new MemberDocument("2", "Jane Doe", "jane@example.com", "password2", "0987654321", "ADMIN");
        
        memberRepository.save(member1);
        memberRepository.save(member2);

        // When
        List<MemberDocument> allMembers = memberRepository.findAll();

        // Then
        assertEquals(2, allMembers.size());
        assertTrue(allMembers.stream().anyMatch(m -> m.getEmail().equals("john@example.com")));
        assertTrue(allMembers.stream().anyMatch(m -> m.getEmail().equals("jane@example.com")));
    }

    @Test
    void testFindById_ExistingMember() {
        // Given
        memberRepository.save(testMember);

        // When
        Optional<MemberDocument> foundMember = memberRepository.findById("1");

        // Then
        assertTrue(foundMember.isPresent());
        assertEquals("1", foundMember.get().getId());
        assertEquals("john@example.com", foundMember.get().getEmail());
    }

    @Test
    void testFindById_NonExistingMember() {
        // When
        Optional<MemberDocument> foundMember = memberRepository.findById("999");

        // Then
        assertFalse(foundMember.isPresent());
    }

    @Test
    void testDeleteById() {
        // Given
        memberRepository.save(testMember);
        assertEquals(1, memberRepository.count());

        // When
        memberRepository.deleteById("1");

        // Then
        assertEquals(0, memberRepository.count());
        assertFalse(memberRepository.findById("1").isPresent());
    }

    @Test
    void testDeleteById_NonExistingMember() {
        // Given
        assertEquals(0, memberRepository.count());

        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> memberRepository.deleteById("999"));
        assertEquals(0, memberRepository.count());
    }

    @Test
    void testCount() {
        // Given
        assertEquals(0, memberRepository.count());

        // When
        memberRepository.save(testMember);

        // Then
        assertEquals(1, memberRepository.count());
    }

    @Test
    void testExistsById_ExistingMember() {
        // Given
        memberRepository.save(testMember);

        // When & Then
        assertTrue(memberRepository.existsById("1"));
    }

    @Test
    void testExistsById_NonExistingMember() {
        // When & Then
        assertFalse(memberRepository.existsById("999"));
    }

    @Test
    void testSaveAll() {
        // Given
        MemberDocument member1 = new MemberDocument("1", "John Doe", "john@example.com", "password1", "1234567890", "USER");
        MemberDocument member2 = new MemberDocument("2", "Jane Doe", "jane@example.com", "password2", "0987654321", "ADMIN");
        List<MemberDocument> members = List.of(member1, member2);

        // When
        List<MemberDocument> savedMembers = memberRepository.saveAll(members);

        // Then
        assertEquals(2, savedMembers.size());
        assertEquals(2, memberRepository.count());
    }

    @Test
    void testFindAllById() {
        // Given
        MemberDocument member1 = new MemberDocument("1", "John Doe", "john@example.com", "password1", "1234567890", "USER");
        MemberDocument member2 = new MemberDocument("2", "Jane Doe", "jane@example.com", "password2", "0987654321", "ADMIN");
        memberRepository.saveAll(List.of(member1, member2));

        // When
        List<MemberDocument> foundMembers = memberRepository.findAllById(List.of("1", "2"));

        // Then
        assertEquals(2, foundMembers.size());
        assertTrue(foundMembers.stream().anyMatch(m -> m.getId().equals("1")));
        assertTrue(foundMembers.stream().anyMatch(m -> m.getId().equals("2")));
    }

    @Test
    void testFindAllById_WithNonExistingIds() {
        // Given
        memberRepository.save(testMember);

        // When
        List<MemberDocument> foundMembers = memberRepository.findAllById(List.of("1", "999"));

        // Then
        assertEquals(1, foundMembers.size());
        assertEquals("1", foundMembers.get(0).getId());
    }

    @Test
    void testDeleteAll() {
        // Given
        MemberDocument member1 = new MemberDocument("1", "John Doe", "john@example.com", "password1", "1234567890", "USER");
        MemberDocument member2 = new MemberDocument("2", "Jane Doe", "jane@example.com", "password2", "0987654321", "ADMIN");
        memberRepository.saveAll(List.of(member1, member2));
        assertEquals(2, memberRepository.count());

        // When
        memberRepository.deleteAll();

        // Then
        assertEquals(0, memberRepository.count());
    }

    @Test
    void testDeleteAllById() {
        // Given
        MemberDocument member1 = new MemberDocument("1", "John Doe", "john@example.com", "password1", "1234567890", "USER");
        MemberDocument member2 = new MemberDocument("2", "Jane Doe", "jane@example.com", "password2", "0987654321", "ADMIN");
        memberRepository.saveAll(List.of(member1, member2));
        assertEquals(2, memberRepository.count());

        // When
        memberRepository.deleteAllById(List.of("1", "2"));

        // Then
        assertEquals(0, memberRepository.count());
    }
} 