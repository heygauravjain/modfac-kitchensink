package com.example.kitchensink.controller;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.example.kitchensink.controller.strategy.RegistrationContext;

import jakarta.validation.Valid;
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

import java.security.Principal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
  public String showAdminHome(Model model, Principal principal, HttpServletResponse response,
                             @RequestParam(value = "clearSession", required = false) String clearSession,
                             HttpSession session) {
    // Clear session attributes if requested
    if ("true".equals(clearSession)) {
      session.removeAttribute("accessToken");
      session.removeAttribute("refreshToken");
      session.removeAttribute("userEmail");
      session.removeAttribute("userRole");
      return "redirect:/admin/home";
    }
    
    // Set cache control headers to prevent back button navigation
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
    
    // Get session tokens for JavaScript access
    String accessToken = (String) session.getAttribute("accessToken");
    String refreshToken = (String) session.getAttribute("refreshToken");
    String sessionUserEmail = (String) session.getAttribute("userEmail");
    String sessionUserRole = (String) session.getAttribute("userRole");
    
    String loggedInUserName;
    
    if (principal != null) {
      loggedInUserName = principal.getName();
    } else {
      // For initial redirect from login, get user info from session
      String userEmail = (String) session.getAttribute("userEmail");
      if (userEmail != null) {
        loggedInUserName = userEmail;
      } else {
        // Default value, will be updated by JavaScript
        loggedInUserName = "Admin";
      }
    }
    
    model.addAttribute("loggedInUser", loggedInUserName);
    model.addAttribute("member", new Member());
    model.addAttribute("members", memberService.getAllMembers());
    
    // Pass session tokens directly to model for JavaScript access
    // Try flash attributes first (from login redirect), then fall back to session
    String userEmail = null;
    String userRole = null;
    
    // Check if we have flash attributes (from login redirect)
    if (model.containsAttribute("accessToken")) {
      accessToken = (String) model.getAttribute("accessToken");
      refreshToken = (String) model.getAttribute("refreshToken");
      userEmail = (String) model.getAttribute("userEmail");
      userRole = (String) model.getAttribute("userRole");
    } else {
      // Fall back to session attributes
      accessToken = (String) session.getAttribute("accessToken");
      refreshToken = (String) session.getAttribute("refreshToken");
      userEmail = (String) session.getAttribute("userEmail");
      userRole = (String) session.getAttribute("userRole");
    }
    
    model.addAttribute("sessionAccessToken", accessToken);
    model.addAttribute("sessionRefreshToken", refreshToken);
    model.addAttribute("sessionUserEmail", userEmail);
    model.addAttribute("sessionUserRole", userRole);
    
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
   * Redirects root path to JWT login page.
   *
   * @return redirect to JWT login
   */
  @GetMapping("/")
  public String redirectToJwtLogin() {
    return "redirect:/jwt-login";
  }

  /**
   * Displays the user profile page.
   *
   * @param model the model
   * @param authentication the authentication object
   * @return the user profile page view
   */
  @GetMapping("/user-profile")
  public String showUserProfile(Model model, Authentication authentication,
                               @RequestParam(value = "clearSession", required = false) String clearSession,
                               HttpSession session) {
    // Clear session attributes if requested
    if ("true".equals(clearSession)) {
      session.removeAttribute("accessToken");
      session.removeAttribute("refreshToken");
      session.removeAttribute("userEmail");
      session.removeAttribute("userRole");
      return "redirect:/user-profile";
    }
    
    // Get session tokens for JavaScript access
    String accessToken = (String) session.getAttribute("accessToken");
    String refreshToken = (String) session.getAttribute("refreshToken");
    String sessionUserEmail = (String) session.getAttribute("userEmail");
    String sessionUserRole = (String) session.getAttribute("userRole");
    
    Member memberToDisplay = null;
    
    // Priority 1: Use session data first (most reliable for current session)
    if (sessionUserEmail != null) {
      // Try to find the member in the database using session email
      MemberDocument memberDocument = memberService.findByEmail(sessionUserEmail).orElse(null);
      if (memberDocument != null) {
        memberToDisplay = memberMapper.memberEntityToMember(memberDocument);
      } else {
        // Create member from session data if not found in database
        memberToDisplay = new Member();
        memberToDisplay.setName(sessionUserEmail.split("@")[0]); // Use email prefix as name
        memberToDisplay.setEmail(sessionUserEmail);
        memberToDisplay.setPhoneNumber(""); // Default empty phone
        memberToDisplay.setRole(sessionUserRole != null ? sessionUserRole : "ROLE_USER");
      }
    }
    
    // Priority 2: Use authenticated user information if no session data
    if (memberToDisplay == null && authentication != null && authentication.isAuthenticated()) {
      String authenticatedEmail = authentication.getName();
      
      // Try to find the member in the database
      MemberDocument memberDocument = memberService.findByEmail(authenticatedEmail).orElse(null);
      if (memberDocument != null) {
        memberToDisplay = memberMapper.memberEntityToMember(memberDocument);
      }
    }
    
    // Priority 3: Create default member if no session or authentication data
    if (memberToDisplay == null) {
      memberToDisplay = new Member();
      memberToDisplay.setName("User");
      memberToDisplay.setEmail("user@example.com");
      memberToDisplay.setPhoneNumber("");
      memberToDisplay.setRole("ROLE_USER");
    }
    
    // Add the member to the model
    model.addAttribute("member", memberToDisplay);
    
    // Add session tokens to model for JavaScript access
    model.addAttribute("sessionAccessToken", accessToken);
    model.addAttribute("sessionRefreshToken", refreshToken);
    model.addAttribute("sessionUserEmail", sessionUserEmail);
    model.addAttribute("sessionUserRole", sessionUserRole);
    
    return "user-profile";
  }

  /**
   * Displays the 403 error page.
   *
   * @return the 403 error page view
   */
  @GetMapping("/403")
  public String error403() {
    return "error/403";
  }

  /**
   * Displays the 401 error page.
   *
   * @return the 401 error page view
   */
  @GetMapping("/401")
  public String error401() {
    return "error/401";
  }
}
