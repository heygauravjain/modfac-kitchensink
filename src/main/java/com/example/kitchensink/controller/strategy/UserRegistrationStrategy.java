package com.example.kitchensink.controller.strategy;

import com.example.kitchensink.entity.MemberEntity;
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
    Optional<MemberEntity> existingMember = memberService.findByEmail(member.getEmail());

    if (existingMember.isPresent()) {
      MemberEntity existingMemberEntity = existingMember.get();
      if (Objects.isNull(existingMemberEntity.getPassword())) {

        /*
          TODO: This should be extended to set Password feature (1st time user).
                  Currently directly updating here for the sake of simplicity & time
         */
        existingMemberEntity.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(existingMemberEntity);

        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        return "redirect:/login";
      } else {
        redirectAttributes.addFlashAttribute("registrationError", true);
        redirectAttributes.addFlashAttribute("errorMessage",
            "Account already exists with this email. Please log in.");
        return "redirect:/login";
      }
    }

    member.setRole("USER");
    member.setPassword(passwordEncoder.encode(member.getPassword()));
    memberService.registerMember(member);

    redirectAttributes.addFlashAttribute("registrationSuccess", true);
    redirectAttributes.addFlashAttribute("successMessage", "User successfully registered!");
    return "redirect:/login";
  }
}
