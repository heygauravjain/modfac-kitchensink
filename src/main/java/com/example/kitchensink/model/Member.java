package com.example.kitchensink.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class Member {

  private String id;

  @NotNull
  @Size(min = 1, max = 25)
  @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
  private String name;

  @NotNull
  @NotEmpty
  @Email
  private String email;

  @NotNull
  @Size(min = 10, max = 12)
  @Digits(fraction = 0, integer = 12)
  private String phoneNumber;

  private String password;

  private String role = "USER";
}
