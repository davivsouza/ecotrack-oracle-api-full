package com.ecotrack.security;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.repository.UserAccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserAccountRepository userAccountRepository;
  private final UserRoleResolver userRoleResolver;

  public JwtAuthenticationFilter(
    JwtService jwtService,
    UserAccountRepository userAccountRepository,
    UserRoleResolver userRoleResolver
  ) {
    this.jwtService = jwtService;
    this.userAccountRepository = userAccountRepository;
    this.userRoleResolver = userRoleResolver;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authorization.substring(7).trim();

    if (token.isEmpty() || !jwtService.isTokenValid(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      UserAccount user = resolveUser(token);

      if (user == null) {
        filterChain.doFilter(request, response);
        return;
      }

      Set<RoleType> roles = userRoleResolver.resolveRoles(user.getEmail());
      AuthenticatedUser principal = new AuthenticatedUser(user.getId(), user.getEmail(), user.getDisplayName(), roles);

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        principal,
        null,
        userRoleResolver.toAuthorities(roles)
      );
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    } catch (Exception ex) {
      log.warn("falha ao autenticar token JWT: {}", ex.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private UserAccount resolveUser(String token) {
    String email = jwtService.extractEmail(token);
    if (email != null && !email.isBlank()) {
      return userAccountRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    String subject = jwtService.extractSubject(token);
    if (subject == null || subject.isBlank()) {
      return null;
    }

    try {
      UUID userId = UUID.fromString(subject);
      return userAccountRepository.findById(userId).orElse(null);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
