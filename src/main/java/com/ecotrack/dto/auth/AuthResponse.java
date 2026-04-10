package com.ecotrack.dto.auth;

public record AuthResponse(
  String token,
  AuthUserResponse user
) {
}
