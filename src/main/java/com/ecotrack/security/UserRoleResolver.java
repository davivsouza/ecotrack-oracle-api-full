package com.ecotrack.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserRoleResolver {

  private final SecurityProperties securityProperties;

  public UserRoleResolver(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  public Set<RoleType> resolveRoles(String email) {
    EnumSet<RoleType> roles = EnumSet.of(RoleType.USER);

    if (email != null && isAdminEmail(email)) {
      roles.add(RoleType.ADMIN);
    }

    return roles;
  }

  public List<GrantedAuthority> toAuthorities(Set<RoleType> roles) {
    return roles.stream()
      .map(RoleType::authority)
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
  }

  public boolean isAdminEmail(String email) {
    String normalized = normalize(email);
    return securityProperties.getAdminEmails().stream()
      .map(this::normalize)
      .anyMatch(normalized::equals);
  }

  private String normalize(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }
}
