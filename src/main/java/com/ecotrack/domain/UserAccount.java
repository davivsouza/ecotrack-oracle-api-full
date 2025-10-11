package com.ecotrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "USERS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {

  @Id
  @Column(name = "ID", columnDefinition = "RAW(16)")
  private UUID id;

  @Email
  @Column(name = "EMAIL", nullable = false, unique = true, length = 200)
  private String email;

  @NotBlank
  @Column(name = "PASSWORD_HASH", nullable = false, length = 200)
  private String passwordHash;

  @Column(name = "DISPLAY_NAME", length = 120)
  private String displayName;

  @Column(name = "CREATED_AT", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "UPDATED_AT", nullable = false)
  private OffsetDateTime updatedAt;
}
