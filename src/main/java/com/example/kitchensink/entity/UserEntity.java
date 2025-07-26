package com.example.kitchensink.entity;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity {

  @Id
  private String id;

  private String username;

  private String password;

  private Set<String> roles; // e.g., ["ROLE_USER", "ROLE_ADMIN"]

  private boolean enabled = true;

}
