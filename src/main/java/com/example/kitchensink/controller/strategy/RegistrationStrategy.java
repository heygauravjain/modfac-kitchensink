package com.example.kitchensink.controller.strategy;

import com.example.kitchensink.model.Member;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface RegistrationStrategy {
  String register(Member member, RedirectAttributes redirectAttributes);
}
