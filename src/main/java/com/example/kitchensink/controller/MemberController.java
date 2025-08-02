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
    // Set cache control headers to prevent back button navigation
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
    
    // Clear session attributes if requested
    if ("true".equals(clearSession)) {
      session.removeAttribute("accessToken");
      session.removeAttribute("refreshToken");
      session.removeAttribute("userEmail");
      session.removeAttribute("userRole");
      log.info("Session attributes cleared as requested");
      return "redirect:/admin/home";
    }
    
    // Debug session attributes
    String sessionAccessToken = (String) session.getAttribute("accessToken");
    String sessionRefreshToken = (String) session.getAttribute("refreshToken");
    String sessionUserEmail = (String) session.getAttribute("userEmail");
    String sessionUserRole = (String) session.getAttribute("userRole");
    
    log.info("Admin dashboard session check: accessToken={}, refreshToken={}, userEmail={}, userRole={}", 
             sessionAccessToken != null ? "Present" : "Missing",
             sessionRefreshToken != null ? "Present" : "Missing",
             sessionUserEmail,
             sessionUserRole);
    
    String loggedInUserName;
    
    if (principal != null) {
      loggedInUserName = principal.getName();
      log.info("Using principal for user: {}", loggedInUserName);
    } else {
      // For initial redirect from login, get user info from session
      String userEmail = (String) session.getAttribute("userEmail");
      if (userEmail != null) {
        loggedInUserName = userEmail;
        log.info("Using session user email: {}", userEmail);
      } else {
        // Default value, will be updated by JavaScript
        loggedInUserName = "Admin";
        log.info("No session user found, using default admin name");
      }
    }
    
    model.addAttribute("loggedInUser", loggedInUserName);
    model.addAttribute("member", new Member());
    model.addAttribute("members", memberService.getAllMembers());
    
    // Debug: Add session attributes to model for debugging
    model.addAttribute("debugAccessToken", session.getAttribute("accessToken") != null ? "Present" : "Missing");
    model.addAttribute("debugRefreshToken", session.getAttribute("refreshToken") != null ? "Present" : "Missing");
    model.addAttribute("debugUserEmail", session.getAttribute("userEmail"));
    model.addAttribute("debugUserRole", session.getAttribute("userRole"));
    
    // Pass session tokens directly to model for JavaScript access
    // Try flash attributes first (from login redirect), then fall back to session
    String accessToken = null;
    String refreshToken = null;
    String userEmail = null;
    String userRole = null;
    
    // Check if we have flash attributes (from login redirect)
    if (model.containsAttribute("accessToken")) {
      accessToken = (String) model.getAttribute("accessToken");
      refreshToken = (String) model.getAttribute("refreshToken");
      userEmail = (String) model.getAttribute("userEmail");
      userRole = (String) model.getAttribute("userRole");
      log.info("Using flash attributes for tokens");
    } else {
      // Fall back to session attributes
      accessToken = (String) session.getAttribute("accessToken");
      refreshToken = (String) session.getAttribute("refreshToken");
      userEmail = (String) session.getAttribute("userEmail");
      userRole = (String) session.getAttribute("userRole");
      log.info("Using session attributes for tokens");
    }
    
    // Debug: Check what model attributes are available
    log.info("Model contains accessToken: {}", model.containsAttribute("accessToken"));
    log.info("Model contains refreshToken: {}", model.containsAttribute("refreshToken"));
    log.info("Model contains userEmail: {}", model.containsAttribute("userEmail"));
    log.info("Model contains userRole: {}", model.containsAttribute("userRole"));
    
    model.addAttribute("sessionAccessToken", accessToken);
    model.addAttribute("sessionRefreshToken", refreshToken);
    model.addAttribute("sessionUserEmail", userEmail);
    model.addAttribute("sessionUserRole", userRole);
    
    log.info("Model attributes set - sessionAccessToken: {}, sessionRefreshToken: {}, sessionUserEmail: {}, sessionUserRole: {}", 
             accessToken != null ? "Present" : "Missing",
             refreshToken != null ? "Present" : "Missing",
             userEmail,
             userRole);
    
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
    
    // Handle case where authentication is null (initial redirect from login)
    if (authentication == null) {
      // For initial redirect, we'll get user info from session
      String userEmail = (String) session.getAttribute("userEmail");
      log.info("Authentication is null, checking session for user email: {}", userEmail);
      
      if (userEmail != null) {
        // Try to find the actual member in the database
        MemberDocument memberDocument = memberService.findByEmail(userEmail).orElse(null);
        if (memberDocument != null) {
          model.addAttribute("member", memberMapper.memberEntityToMember(memberDocument));
          log.info("Found member document for user: {}", userEmail);
          log.info("Member data set - Name: {}, Email: {}, Role: {}", 
                   memberDocument.getName(), memberDocument.getEmail(), memberDocument.getRole());
        } else {
          // If member not found in database, create a member object from session data
          Member sessionMember = new Member();
          sessionMember.setName(userEmail.split("@")[0]); // Use email prefix as name
          sessionMember.setEmail(userEmail);
          sessionMember.setPhoneNumber(""); // Default empty phone
          sessionMember.setRole((String) session.getAttribute("userRole"));
          model.addAttribute("member", sessionMember);
          log.info("Created member from session data for user: {}", userEmail);
          log.info("Session member data set - Name: {}, Email: {}, Role: {}", 
                   sessionMember.getName(), sessionMember.getEmail(), sessionMember.getRole());
        }
      } else {
        // Create a default member object only if no session data
        Member defaultMember = new Member();
        defaultMember.setName("User");
        defaultMember.setEmail("user@example.com");
        defaultMember.setPhoneNumber("");
        defaultMember.setRole("USER");
        model.addAttribute("member", defaultMember);
        log.info("No session user found, using default user");
      }
    } else {
      // Normal case - user is authenticated
      String email = authentication.getName();
      log.info("User is authenticated: {}", email);
      MemberDocument memberDocument = memberService.findByEmail(email).orElse(null);
      if (memberDocument != null) {
        model.addAttribute("member", memberMapper.memberEntityToMember(memberDocument));
      } else {
        // Fallback to session data if database lookup fails
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail != null) {
          Member sessionMember = new Member();
          sessionMember.setName(userEmail.split("@")[0]);
          sessionMember.setEmail(userEmail);
          sessionMember.setPhoneNumber("");
          sessionMember.setRole((String) session.getAttribute("userRole"));
          model.addAttribute("member", sessionMember);
        }
      }
    }
    
    // Pass session tokens directly to model for JavaScript access
    // Try flash attributes first (from login redirect), then fall back to session
    String accessToken = null;
    String refreshToken = null;
    String userEmail = null;
    String userRole = null;
    
    // Check if we have flash attributes (from login redirect)
    if (model.containsAttribute("accessToken")) {
      accessToken = (String) model.getAttribute("accessToken");
      refreshToken = (String) model.getAttribute("refreshToken");
      userEmail = (String) model.getAttribute("userEmail");
      userRole = (String) model.getAttribute("userRole");
      log.info("Using flash attributes for tokens");
    } else {
      // Fall back to session attributes
      accessToken = (String) session.getAttribute("accessToken");
      refreshToken = (String) session.getAttribute("refreshToken");
      userEmail = (String) session.getAttribute("userEmail");
      userRole = (String) session.getAttribute("userRole");
      log.info("Using session attributes for tokens");
    }
    
    model.addAttribute("sessionAccessToken", accessToken);
    model.addAttribute("sessionRefreshToken", refreshToken);
    model.addAttribute("sessionUserEmail", userEmail);
    model.addAttribute("sessionUserRole", userRole);
    
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
