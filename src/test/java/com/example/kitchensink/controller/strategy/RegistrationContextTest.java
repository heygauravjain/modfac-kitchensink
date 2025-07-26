package com.example.kitchensink.controller.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class RegistrationContextTest {

  @Mock
  private UserRegistrationStrategy userRegistrationStrategy;

  @Mock
  private AdminRegistrationStrategy adminRegistrationStrategy;

  @Mock
  private RedirectAttributes redirectAttributes;

  @InjectMocks
  private RegistrationContext registrationContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSetStrategy_WithIndexSourcePage() {
    // Act: Set strategy with "index" as the source page
    registrationContext.setStrategy("index");

    // Assert: Verify that the strategy is set to AdminRegistrationStrategy
    verifyNoInteractions(userRegistrationStrategy);
    registrationContext.register(new Member(), redirectAttributes);
    verify(adminRegistrationStrategy, times(1)).register(any(Member.class),
        any(RedirectAttributes.class));
  }

  @Test
  void testSetStrategy_WithRegisterSourcePage() {
    // Act: Set strategy with "register" as the source page
    registrationContext.setStrategy("register");

    // Assert: Verify that the strategy is set to UserRegistrationStrategy
    verifyNoInteractions(adminRegistrationStrategy);
    registrationContext.register(new Member(), redirectAttributes);
    verify(userRegistrationStrategy, times(1)).register(any(Member.class),
        any(RedirectAttributes.class));
  }

  @Test
  void testRegister_UsingAdminRegistrationStrategy() {
    // Arrange: Set the strategy to AdminRegistrationStrategy
    registrationContext.setStrategy(RegistrationContext.INDEX);
    Member member = new Member();
    when(adminRegistrationStrategy.register(any(Member.class), any(RedirectAttributes.class)))
        .thenReturn("redirect:/admin");

    // Act: Call the register method
    String result = registrationContext.register(member, redirectAttributes);

    // Assert: Verify that AdminRegistrationStrategy's register method is called
    verify(adminRegistrationStrategy, times(1)).register(member, redirectAttributes);
    assertEquals("redirect:/admin", result);
  }

  @Test
  void testRegister_UsingUserRegistrationStrategy() {
    // Arrange: Set the strategy to UserRegistrationStrategy
    registrationContext.setStrategy(RegistrationContext.REGISTER);
    Member member = new Member();
    when(userRegistrationStrategy.register(any(Member.class), any(RedirectAttributes.class)))
        .thenReturn("redirect:/user");

    // Act: Call the register method
    String result = registrationContext.register(member, redirectAttributes);

    // Assert: Verify that UserRegistrationStrategy's register method is called
    verify(userRegistrationStrategy, times(1)).register(member, redirectAttributes);
    assertEquals("redirect:/user", result);
  }
}
