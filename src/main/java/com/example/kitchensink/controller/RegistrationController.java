package com.example.kitchensink.controller;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {


  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public RegistrationController(MemberService memberService, MemberRepository memberRepository,
      BCryptPasswordEncoder passwordEncoder) {
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/user-register")
  public String registerUser(
      @Valid @ModelAttribute("member") Member member,
      RedirectAttributes redirectAttributes) {

    // Check if the member already exists
    MemberEntity existingMember = memberService.findByEmail(member.getEmail());

    if (existingMember != null) {
      if (existingMember.getPassword() == null) {
        existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(existingMember);

        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        return "redirect:/login";
      } else {
        // Password is already set, show error message
        redirectAttributes.addFlashAttribute("registrationError", true);
        redirectAttributes.addFlashAttribute("errorMessage",
            "Account already exists with this email. Please log in.");
        return "redirect:/login";
      }
    }

    // Create a new member and save
    member.setRole("USER");
    member.setPassword(passwordEncoder.encode(member.getPassword()));
    memberService.registerMember(member);

    redirectAttributes.addFlashAttribute("registrationSuccess", true);
    redirectAttributes.addFlashAttribute("successMessage", "Member successfully registered!");

    // Redirect to set-password page
    return "redirect:/login";
  }
}
