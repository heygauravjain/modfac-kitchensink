package com.example.kitchensink.controller.strategy;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class UserRegistrationStrategy implements RegistrationStrategy {

  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public UserRegistrationStrategy(MemberService memberService, MemberRepository memberRepository,
      BCryptPasswordEncoder passwordEncoder) {
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public String register(Member member, RedirectAttributes redirectAttributes) {
    Optional<MemberDocument> existingMember = memberService.findByEmail(member.getEmail());

    if (existingMember.isPresent()) {
      MemberDocument existingMemberDocument = existingMember.get();
      if (Objects.isNull(existingMemberDocument.getPassword())) {

        /*
          TODO: This should be extended to set Password feature (1st time user).
                  Currently directly updating here for the sake of simplicity & time
         */
        existingMemberDocument.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(existingMemberDocument);

        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        return "redirect:/jwt-login";
      } else {
        redirectAttributes.addFlashAttribute("registrationError", true);
        redirectAttributes.addFlashAttribute("errorMessage",
            "Account already exists with this email. Please log in.");
        return "redirect:/jwt-login";
      }
    }

    member.setRole("USER");
    member.setPassword(passwordEncoder.encode(member.getPassword()));
    memberService.registerMember(member);

    redirectAttributes.addFlashAttribute("registrationSuccess", true);
    redirectAttributes.addFlashAttribute("successMessage", "User successfully registered!");
    return "redirect:/jwt-login";
  }
}
