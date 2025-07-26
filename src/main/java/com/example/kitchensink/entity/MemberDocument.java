package com.example.kitchensink.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "members")
@AllArgsConstructor
@NoArgsConstructor
public class MemberDocument implements Serializable {

  @Id
  private String id;

  private String name;

  @Indexed(unique = true)
  private String email;

  private String phoneNumber;

  private String password;

  private String role;

}
