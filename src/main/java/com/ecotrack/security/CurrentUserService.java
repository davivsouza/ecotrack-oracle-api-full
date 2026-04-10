package com.ecotrack.security;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.UserAccountRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class CurrentUserService {

  private final UserAccountRepository userAccountRepository;

  public CurrentUserService(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  public AuthenticatedUser requireAuthenticatedPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
      throw new ResponseStatusException(UNAUTHORIZED, "usuário não autenticado");
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof AuthenticatedUser authenticatedUser) {
      return authenticatedUser;
    }

    throw new ResponseStatusException(UNAUTHORIZED, "token inválido ou expirado");
  }

  public UserAccount requireCurrentUser() {
    AuthenticatedUser principal = requireAuthenticatedPrincipal();
    return userAccountRepository.findByEmail(principal.email())
      .orElseThrow(() -> new ResourceNotFoundException("usuário autenticado não encontrado"));
  }

  public UserAccount resolveActingUser(String requestedEmail) {
    UserAccount currentUser = requireCurrentUser();

    if (requestedEmail == null || requestedEmail.isBlank()) {
      return currentUser;
    }

    if (currentUser.getEmail().equalsIgnoreCase(requestedEmail)) {
      return currentUser;
    }

    if (hasRole(RoleType.ADMIN)) {
      return userAccountRepository.findByEmail(requestedEmail)
        .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + requestedEmail));
    }

    throw new AccessDeniedException("você não tem permissão para acessar dados de outro usuário");
  }

  public boolean hasRole(RoleType role) {
    AuthenticatedUser principal = requireAuthenticatedPrincipal();
    return principal.roles().contains(role);
  }
}
