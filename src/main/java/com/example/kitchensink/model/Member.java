package com.example.kitchensink.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class Member {

  private String id;

  private String name;

  private String email;

  private String phoneNumber;

  private String password;

  private String role = "USER";
}
