package com.ecotrack.security;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
  UUID id,
  String email,
  String displayName,
  Set<RoleType> roles
) {
}
