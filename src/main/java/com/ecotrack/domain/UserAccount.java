package com.ecotrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

  @Id
  @Column(name = "ID", columnDefinition = "RAW(16)")
  private UUID id;

  @Email
  @NotBlank
  @Column(name = "EMAIL", nullable = false, unique = true, length = 200)
  private String email;

  @NotBlank
  @Column(name = "PASSWORD_HASH", nullable = false, length = 200)
  private String passwordHash;

  @NotBlank
  @Column(name = "DISPLAY_NAME", length = 120, nullable = false)
  private String displayName;

  @Enumerated(EnumType.STRING)
  @Column(name = "ROLE", nullable = false, length = 20)
  private UserRole role;

  @Column(name = "CREATED_AT", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "UPDATED_AT", nullable = false)
  private OffsetDateTime updatedAt;
}
