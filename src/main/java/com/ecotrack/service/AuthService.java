package com.ecotrack.service;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.dto.auth.AuthLoginRequest;
import com.ecotrack.dto.auth.AuthRegisterRequest;
import com.ecotrack.dto.auth.AuthResponse;
import com.ecotrack.dto.auth.AuthUserResponse;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.UserAccountRepository;
import com.ecotrack.security.AuthenticatedUser;
import com.ecotrack.security.JwtService;
import com.ecotrack.security.RoleType;
import com.ecotrack.security.UserRoleResolver;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {

  private static final Pattern STRONG_PASSWORD = Pattern.compile("^.{6,64}$");

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserRoleResolver userRoleResolver;
  private final ExternalIdCodec externalIdCodec;

  public AuthService(
    UserAccountRepository userAccountRepository,
    PasswordEncoder passwordEncoder,
    JwtService jwtService,
    UserRoleResolver userRoleResolver,
    ExternalIdCodec externalIdCodec
  ) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.userRoleResolver = userRoleResolver;
    this.externalIdCodec = externalIdCodec;
  }

  @Transactional
  public AuthResponse register(AuthRegisterRequest request) {
    String normalizedEmail = normalizeEmail(request.email());
    String normalizedName = normalizeName(request.name());

    if (userAccountRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
      throw new ResourceConflictException("Email já cadastrado.");
    }

    if (normalizedName.length() < 3) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos.");
    }

    validatePassword(request.password());

    OffsetDateTime now = OffsetDateTime.now();
    UserAccount user = UserAccount.builder()
      .id(UUID.randomUUID())
      .email(normalizedEmail)
      .passwordHash(passwordEncoder.encode(request.password()))
      .displayName(normalizedName)
      .createdAt(now)
      .updatedAt(now)
      .build();

    UserAccount saved = userAccountRepository.save(user);
    return buildAuthResponse(saved);
  }

  public AuthResponse login(AuthLoginRequest request) {
    String normalizedEmail = normalizeEmail(request.email());
    UserAccount user = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha incorretos."));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha incorretos.");
    }

    return buildAuthResponse(user);
  }

  public AuthUserResponse me(AuthenticatedUser authenticatedUser) {
    UserAccount user = userAccountRepository.findById(authenticatedUser.id())
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    return toUserResponse(user.getId(), user.getEmail(), user.getDisplayName());
  }

  private AuthResponse buildAuthResponse(UserAccount user) {
    Set<RoleType> roles = userRoleResolver.resolveRoles(user.getEmail());
    String token = jwtService.generateToken(user, roles);

    return new AuthResponse(token, toUserResponse(user.getId(), user.getEmail(), user.getDisplayName()));
  }

  private AuthUserResponse toUserResponse(UUID id, String email, String displayName) {
    return new AuthUserResponse(externalIdCodec.userId(id), displayName, email);
  }

  private String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeName(String name) {
    return name == null ? "" : name.trim();
  }

  private void validatePassword(String password) {
    if (password == null || !STRONG_PASSWORD.matcher(password).matches()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos.");
    }
  }
}
