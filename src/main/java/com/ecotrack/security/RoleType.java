package com.ecotrack.security;

public enum RoleType {
  USER,
  ADMIN;

  public String authority() {
    return "ROLE_" + name();
  }
}
