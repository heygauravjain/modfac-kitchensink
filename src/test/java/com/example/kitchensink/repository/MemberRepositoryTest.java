package com.example.kitchensink.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.kitchensink.entity.MemberEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ActiveProfiles("test")// Use DataMongoTest to configure an embedded MongoDB for testing
@ExtendWith(SpringExtension.class) // Use JUnit 5's SpringExtension to enable Spring context
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  private MemberEntity member1;
  private MemberEntity member2;

  @BeforeEach
  void setUp() {
    // Arrange: Create sample MemberEntity objects
    member1 = new MemberEntity("1", "Alice", "alice@example.com", "1234567890", "password1",
        "USER");
    member2 = new MemberEntity("2", "Bob", "bob@example.com", "0987654321", "password2", "ADMIN");

    // Clear any existing data in the repository and add sample members to the embedded database
    memberRepository.deleteAll();
    memberRepository.save(member1);
    memberRepository.save(member2);
  }

  @Test
  void testFindByEmail() {
    // Act: Find a member by email
    Optional<MemberEntity> foundMember = memberRepository.findByEmail("alice@example.com");

    // Assert: Verify the member details
    assertThat(foundMember).isPresent();
    assertThat(foundMember.get().getName()).isEqualTo("Alice");
    assertThat(foundMember.get().getEmail()).isEqualTo("alice@example.com");
  }

  @Test
  void testFindAllOrderedByName() {
    // Act: Find all members ordered by name
    List<MemberEntity> members = memberRepository.findAll();

    // Assert: Verify the order and details of the members
    assertThat(members).hasSize(2);
    assertThat(members.get(0).getName()).isEqualTo("Alice");
    assertThat(members.get(1).getName()).isEqualTo("Bob");
  }

  @Test
  void testFindByNonExistingEmail() {
    // Act: Try to find a member by a non-existing email
    Optional<MemberEntity> foundMember = memberRepository.findByEmail("nonexistent@example.com");

    // Assert: Verify that no member is found
    assertThat(foundMember).isNotPresent();
  }

  @Test
  void testSaveMember() {
    // Arrange: Create a new MemberEntity
    MemberEntity newMember = new MemberEntity("3", "Charlie", "charlie@example.com", "1122334455",
        "password3", "USER");

    // Act: Save the new member
    MemberEntity savedMember = memberRepository.save(newMember);

    // Assert: Verify that the member was saved correctly
    assertThat(savedMember).isNotNull();
    assertThat(savedMember.getId()).isEqualTo("3");
    assertThat(savedMember.getName()).isEqualTo("Charlie");
    assertThat(savedMember.getEmail()).isEqualTo("charlie@example.com");
  }

}
