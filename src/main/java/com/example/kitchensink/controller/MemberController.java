package com.example.kitchensink.controller;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.mapper.MemberMapper;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.example.kitchensink.repository.MemberRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Optional;

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

  /** The member repository */
  private final MemberRepository memberRepository;

  /** The password encoder */
  private final BCryptPasswordEncoder passwordEncoder;

  /** The member mapper */
  private final MemberMapper memberMapper = MemberMapper.INSTANCE;

  // Session attribute names as constants
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String REFRESH_TOKEN = "refreshToken";
  private static final String USER_EMAIL = "userEmail";
  private static final String USER_ROLE = "userRole";

     /**
     * Member controller constructor
     *
     * @param memberService
     * @param memberRepository
     * @param passwordEncoder
     */
  @Autowired
  public MemberController(MemberService memberService, MemberRepository memberRepository, 
                        BCryptPasswordEncoder passwordEncoder) {
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
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
      clearSessionAttributes(session);
      return "redirect:/admin/home";
    }
    
    // Set cache control headers to prevent back button navigation
    setCacheControlHeaders(response);
    
    // Get session tokens for JavaScript access
    SessionData sessionData = getSessionData(session, model);
    
    String loggedInUserName = getLoggedInUserName(principal, sessionData.getUserEmail());
    
    model.addAttribute("loggedInUser", loggedInUserName);
    model.addAttribute("member", new Member());
    model.addAttribute("members", memberService.getAllMembers());
    
    // Add session data to model
    addSessionDataToModel(model, sessionData);
    
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
    
    try {
      Optional<MemberDocument> existingMember = memberService.findByEmail(member.getEmail());

      if (existingMember.isPresent()) {
        MemberDocument existingMemberDocument = existingMember.get();
        
        // Admin registration from admin dashboard
        if ("index".equalsIgnoreCase(source)) {
          redirectAttributes.addFlashAttribute("registrationError", true);
          redirectAttributes.addFlashAttribute("errorMessage", "Member already registered with this email!");
          return "redirect:/admin/home";
        }
        
        // User registration from registration page
        if (existingMemberDocument.getPassword() == null) {
          // First time user - set password
          existingMemberDocument.setPassword(passwordEncoder.encode(member.getPassword()));
          memberRepository.save(existingMemberDocument);

          redirectAttributes.addFlashAttribute("registrationSuccess", true);
          redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
          return "redirect:/jwt-login";
        } else {
          redirectAttributes.addFlashAttribute("registrationError", true);
          redirectAttributes.addFlashAttribute("errorMessage", "Account already exists with this email. Please log in.");
          return "redirect:/jwt-login";
        }
      }

      // New user registration
      if ("index".equalsIgnoreCase(source)) {
        // Admin registration - let service handle password encoding
        memberService.registerMember(member);
        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        redirectAttributes.addFlashAttribute("successMessage", "Member successfully registered!");
        return "redirect:/admin/home";
      } else {
        // User registration - let service handle password encoding
        member.setRole("USER");
        memberService.registerMember(member);

        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        redirectAttributes.addFlashAttribute("successMessage", "User successfully registered!");
        return "redirect:/jwt-login";
      }

    } catch (Exception e) {
      String errorMessage = getRootErrorMessage(e);
      log.error(errorMessage);

      redirectAttributes.addFlashAttribute("registrationError", true);
      redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
      
      return "index".equalsIgnoreCase(source) ? "redirect:/admin/home" : "redirect:/jwt-login";
    }
  }

  /**
   * Helper method to get root error message
   */
  private String getRootErrorMessage(Exception e) {
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
      clearSessionAttributes(session);
      return "redirect:/user-profile";
    }
    
    // Get session tokens for JavaScript access
    SessionData sessionData = getSessionData(session, model);
    
    Member memberToDisplay = getMemberToDisplay(sessionData.getUserEmail(), authentication);
    
    // Add the member to the model
    model.addAttribute("member", memberToDisplay);
    
    // Add session tokens to model for JavaScript access
    addSessionDataToModel(model, sessionData);
    
    return "user-profile";
  }

  /**
   * Helper method to clear session attributes
   */
  private void clearSessionAttributes(HttpSession session) {
    session.removeAttribute(ACCESS_TOKEN);
    session.removeAttribute(REFRESH_TOKEN);
    session.removeAttribute(USER_EMAIL);
    session.removeAttribute(USER_ROLE);
  }

  /**
   * Helper method to set cache control headers
   */
  private void setCacheControlHeaders(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
  }

  /**
   * Helper method to get session data
   */
  private SessionData getSessionData(HttpSession session, Model model) {
    String accessToken = (String) session.getAttribute(ACCESS_TOKEN);
    String refreshToken = (String) session.getAttribute(REFRESH_TOKEN);
    String userEmail = (String) session.getAttribute(USER_EMAIL);
    String userRole = (String) session.getAttribute(USER_ROLE);
    
    // Check if we have flash attributes (from login redirect)
    if (model.containsAttribute(ACCESS_TOKEN)) {
      accessToken = (String) model.getAttribute(ACCESS_TOKEN);
      refreshToken = (String) model.getAttribute(REFRESH_TOKEN);
      userEmail = (String) model.getAttribute(USER_EMAIL);
      userRole = (String) model.getAttribute(USER_ROLE);
    }
    
    return new SessionData(accessToken, refreshToken, userEmail, userRole);
  }

  /**
   * Helper method to get logged in user name
   */
  private String getLoggedInUserName(Principal principal, String sessionUserEmail) {
    if (principal != null) {
      return principal.getName();
    } else if (sessionUserEmail != null) {
      return sessionUserEmail;
    } else {
      return "Admin"; // Default value, will be updated by JavaScript
    }
  }

  /**
   * Helper method to get member to display
   */
  private Member getMemberToDisplay(String sessionUserEmail, Authentication authentication) {
    // Priority 1: Use session data first (most reliable for current session)
    if (sessionUserEmail != null) {
      // Try to find the member in the database using session email
      MemberDocument memberDocument = memberService.findByEmail(sessionUserEmail).orElse(null);
      if (memberDocument != null) {
        return memberMapper.memberEntityToMember(memberDocument);
      } else {
        // Create member from session data if not found in database
        Member member = new Member();
        member.setName(sessionUserEmail.split("@")[0]); // Use email prefix as name
        member.setEmail(sessionUserEmail);
        member.setPhoneNumber(""); // Default empty phone
        member.setRole("ROLE_USER");
        return member;
      }
    }
    
    // Priority 2: Use authenticated user information if no session data
    if (authentication != null && authentication.isAuthenticated()) {
      String authenticatedEmail = authentication.getName();
      
      // Try to find the member in the database
      MemberDocument memberDocument = memberService.findByEmail(authenticatedEmail).orElse(null);
      if (memberDocument != null) {
        return memberMapper.memberEntityToMember(memberDocument);
      }
    }
    
    // Priority 3: Create default member if no session or authentication data
    Member member = new Member();
    member.setName("User");
    member.setEmail("user@example.com");
    member.setPhoneNumber("");
    member.setRole("ROLE_USER");
    return member;
  }

  /**
   * Helper method to add session data to model
   */
  private void addSessionDataToModel(Model model, SessionData sessionData) {
    model.addAttribute("sessionAccessToken", sessionData.getAccessToken());
    model.addAttribute("sessionRefreshToken", sessionData.getRefreshToken());
    model.addAttribute("sessionUserEmail", sessionData.getUserEmail());
    model.addAttribute("sessionUserRole", sessionData.getUserRole());
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

  /**
   * Displays the 500 error page.
   *
   * @return the 500 error page view
   */
  @GetMapping("/500")
  public String error500() {
    return "error/500";
  }

  /**
   * Inner class to hold session data
   */
  private static class SessionData {
    private final String accessToken;
    private final String refreshToken;
    private final String userEmail;
    private final String userRole;

    public SessionData(String accessToken, String refreshToken, String userEmail, String userRole) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.userEmail = userEmail;
      this.userRole = userRole;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getUserEmail() { return userEmail; }
    public String getUserRole() { return userRole; }
  }
}
