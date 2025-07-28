package com.example.kitchensink.controller;

import com.example.kitchensink.controller.strategy.RegistrationContext;
import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The Class MemberController.
 *
 * @author Gaurav Jain
 */
@Controller
@Slf4j
public class MemberController {

   /** The member service */
  private final MemberService memberService;

  /** The registration context */
  private final RegistrationContext registrationContext;

  /** The member mapper */
  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

     /**
     * Member controller constructor
     *
     * @param memberService
     * @param registrationContext
     */
  @Autowired
  public MemberController(MemberService memberService, RegistrationContext registrationContext) {
    this.memberService = memberService;
    this.registrationContext = registrationContext;
  }

  /**
   * Displays the admin home page.
   *
   * @param model the model
   * @param principal the principal
   * @return the admin home page view
   */
  @GetMapping("/admin/home")
  public String showAdminHome(Model model, Principal principal) {
    String loggedInUserName = principal.getName();
    model.addAttribute("loggedInUser", loggedInUserName);
    model.addAttribute("member", new Member());
    model.addAttribute("members", memberService.getAllMembers());
    return "index";
  }

  /**
   * Displays the registration page.
   *
   * @param model the model
   * @return the registration page view
   */
  @PostMapping("/register")
  public String registerMember(
      @Valid @ModelAttribute("member") Member member,
      @RequestParam(value = "sourcePage", required = false) String source,
      RedirectAttributes redirectAttributes) {

    registrationContext.setStrategy(source);
    return registrationContext.register(member, redirectAttributes);
  }

  /**
   * Displays the login page.
   *
   * @return the login page view
   */
  @GetMapping({"/", "/login"})
  public String showLoginPage() {
    return "login";
  }

  /**
   * Displays the user profile page.
   *
   * @param model the model
   * @param authentication the authentication object
   * @return the user profile page view
   */
  @GetMapping("/user-profile")
  public String showUserProfile(Model model, Authentication authentication) {
    String email = authentication.getName();
    MemberDocument memberDocument = memberService.findByEmail(email).orElse(null);

    // Add the memberDocument details to the model to display in the user profile
    model.addAttribute("member", memberMapper.memberEntityToMember(memberDocument));
    return "user-profile";
  }

  /**
   * Displays the 403 error page.
   *
   * @return the 403 error page view
   */
  @GetMapping("/403")
  public String error403() {
    return "/error/403";
  }

  /**
   * Displays the 401 error page.
   *
   * @return the 401 error page view
   */
  @GetMapping("/401")
  public String error401() {
    return "/error/401";
  }
}
