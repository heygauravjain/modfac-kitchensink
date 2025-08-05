package com.example.kitchensink.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

  private String id;

  @NotNull
  @Size(min = ValidationPatterns.NAME_MIN_LENGTH, max = ValidationPatterns.NAME_MAX_LENGTH)
  @Pattern(regexp = ValidationPatterns.NAME_PATTERN, message = ValidationPatterns.NAME_MESSAGE)
  private String name;

  @NotNull
  @NotEmpty
  @Email
  private String email;

  @Size(min = ValidationPatterns.PASSWORD_MIN_LENGTH, message = ValidationPatterns.SIMPLE_PASSWORD_MESSAGE)
  private String password;

  // Made phone number truly optional - only validate if not empty
  @Pattern(regexp = ValidationPatterns.PHONE_PATTERN, message = ValidationPatterns.PHONE_MESSAGE)
  private String phoneNumber;

  private String role = "USER";
}
