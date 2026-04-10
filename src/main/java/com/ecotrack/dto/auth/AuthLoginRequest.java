package com.ecotrack.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
  @NotBlank @Email String email,
  @NotBlank @Size(min = 6, max = 64) String password
) {
}
