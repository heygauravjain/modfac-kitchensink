package com.example.kitchensink.model;

import jakarta.validation.constraints.Digits;
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
  @Size(min = 1, max = 25)
  @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
  private String name;

  @NotNull
  @NotEmpty
  @Email
  private String email;

  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  // Made phone number truly optional - only validate if not empty
  @Pattern(regexp = "^$|^\\d{10,12}$", message = "Phone number must be 10-12 digits or empty")
  private String phoneNumber;

  private String role = "USER";
}
