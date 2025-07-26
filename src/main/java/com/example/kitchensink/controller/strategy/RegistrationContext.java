package com.example.kitchensink.controller.strategy;

import com.example.kitchensink.model.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class RegistrationContext {

  public static final String INDEX = "index";
  public static final String REGISTER = "register";
  private RegistrationStrategy registrationStrategy;

  private final UserRegistrationStrategy userRegistrationStrategy;

  private final AdminRegistrationStrategy adminRegistrationStrategy;

  public RegistrationContext(UserRegistrationStrategy userRegistrationStrategy,
      AdminRegistrationStrategy adminRegistrationStrategy) {
    this.userRegistrationStrategy = userRegistrationStrategy;
    this.adminRegistrationStrategy = adminRegistrationStrategy;
  }

  public void setStrategy(String sourcePage) {
    if (INDEX.equalsIgnoreCase(sourcePage)) {
      this.registrationStrategy = adminRegistrationStrategy;
    } else if (REGISTER.equalsIgnoreCase(sourcePage)) {
      this.registrationStrategy = userRegistrationStrategy;
    }
  }

  public String register(Member member, RedirectAttributes redirectAttributes) {
    return registrationStrategy.register(member, redirectAttributes);
  }
}
