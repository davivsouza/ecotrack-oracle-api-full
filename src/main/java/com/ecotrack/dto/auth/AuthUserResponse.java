package com.ecotrack.dto.auth;

public record AuthUserResponse(
  String id,
  String name,
  String email
) {
}
