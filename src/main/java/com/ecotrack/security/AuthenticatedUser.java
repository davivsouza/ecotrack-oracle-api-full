package com.ecotrack.security;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.domain.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AuthenticatedUser implements UserDetails {
  private final UserAccount account;

  public AuthenticatedUser(UserAccount account) {
    this.account = account;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(account.getRole().name()));
  }

  @Override
  public String getPassword() {
    return account.getPasswordHash();
  }

  @Override
  public String getUsername() {
    return account.getEmail();
  }

  public String getDisplayName() {
    return account.getDisplayName();
  }

  public UserRole getRole() {
    return account.getRole();
  }
}
