package com.ecotrack.security;

import com.ecotrack.domain.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

  private final SecurityProperties securityProperties;
  private SecretKey signingKey;

  public JwtService(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  @PostConstruct
  void init() {
    String secret = securityProperties.getJwtSecret();
    byte[] keyBytes = hashSecret(secret);
    signingKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(UserAccount user, Set<RoleType> roles) {
    Instant now = Instant.now();
    Instant expiration = now.plusSeconds(getExpirationSeconds());

    List<String> authorities = roles.stream()
      .map(RoleType::authority)
      .toList();

    return Jwts.builder()
      .subject(user.getId() == null ? null : user.getId().toString())
      .claim("email", user.getEmail())
      .claim("name", user.getDisplayName())
      .claim("roles", authorities)
      .issuedAt(Date.from(now))
      .expiration(Date.from(expiration))
      .signWith(signingKey)
      .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser()
      .verifyWith(signingKey)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public boolean isTokenValid(String token) {
    try {
      parse(token);
      return true;
    } catch (JwtException | IllegalArgumentException ex) {
      return false;
    }
  }

  public String extractSubject(String token) {
    return parse(token).getSubject();
  }

  public String extractEmail(String token) {
    Object claim = parse(token).get("email");
    if (claim == null) {
      return null;
    }
    return String.valueOf(claim);
  }

  public Set<RoleType> extractRoles(String token) {
    Object rolesClaim = parse(token).get("roles");
    if (!(rolesClaim instanceof List<?> rolesList)) {
      return Set.of();
    }

    return rolesList.stream()
      .filter(String.class::isInstance)
      .map(String.class::cast)
      .map(this::authorityToRole)
      .collect(Collectors.toSet());
  }

  public long getExpirationSeconds() {
    return Math.max(securityProperties.getJwtExpirationMinutes(), 1) * 60;
  }

  private RoleType authorityToRole(String authority) {
    String normalized = authority == null ? "" : authority.trim().toUpperCase();
    if (normalized.startsWith("ROLE_")) {
      normalized = normalized.substring("ROLE_".length());
    }

    try {
      return RoleType.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      return RoleType.USER;
    }
  }

  private byte[] hashSecret(String secret) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("algoritmo SHA-256 indisponível", ex);
    }
  }
}
