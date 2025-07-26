package com.example.kitchensink.controller.strategy;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
@Slf4j
public class AdminRegistrationStrategy implements RegistrationStrategy {

  private final MemberService memberService;

  @Autowired
  public AdminRegistrationStrategy(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public String register(Member member, RedirectAttributes redirectAttributes) {
    try {
      boolean existingMember = memberService.findByEmail(member.getEmail()).isPresent();

      if (existingMember) {
        redirectAttributes.addFlashAttribute("registrationError", true);
        redirectAttributes.addFlashAttribute("errorMessage",
            "Member already registered with this email!");
        return "redirect:/members";
      }
      memberService.registerMember(member);
      redirectAttributes.addFlashAttribute("registrationSuccess", true);
      redirectAttributes.addFlashAttribute("successMessage", "Member successfully registered!");

    } catch (Exception e) {
      String errorMessage = getRootErrorMessage(e);
      log.error(errorMessage);

      redirectAttributes.addFlashAttribute("registrationError", true);
      redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
    }
    return "redirect:/members";
  }

  public String getRootErrorMessage(Exception e) {
    String errorMessage = "Registration failed. See server log for more information";
    if (e == null) {
      return errorMessage;
    }
    Throwable t = e;
    while (t.getCause() != null) {
      t = t.getCause();
    }
    return t.getLocalizedMessage();
  }
}
