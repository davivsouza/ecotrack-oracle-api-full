package com.ecotrack.service;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.domain.UserRole;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.UserAccountRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserAccountRepository repo;

  public Page<UserAccount> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public List<UserAccount> list() {
    return repo.findAll();
  }

  public UserAccount get(UUID id) {
    return repo.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com id: " + id));
  }

  public UserAccount findByEmail(String email) {
    return repo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
  }

  @Transactional
  public UserAccount create(@Valid UserAccount user) {
    if (repo.findByEmail(user.getEmail()).isPresent()) {
      throw new ResourceConflictException("email já cadastrado: " + user.getEmail());
    }

    if (user.getId() == null) {
      user.setId(UUID.randomUUID());
    }

    if (user.getRole() == null) {
      user.setRole(UserRole.ROLE_CONSUMER);
    }

    OffsetDateTime now = OffsetDateTime.now();
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    return repo.save(user);
  }

  @Transactional
  public UserAccount update(UUID id, @Valid UserAccount user) {
    UserAccount db = get(id);

    if (!db.getEmail().equals(user.getEmail()) && repo.findByEmail(user.getEmail()).isPresent()) {
      throw new ResourceConflictException("email já cadastrado: " + user.getEmail());
    }

    db.setEmail(user.getEmail());
    db.setPasswordHash(user.getPasswordHash());
    db.setDisplayName(user.getDisplayName());
    db.setRole(user.getRole() == null ? db.getRole() : user.getRole());
    db.setUpdatedAt(OffsetDateTime.now());
    return repo.save(db);
  }

  @Transactional
  public void delete(UUID id) {
    if (!repo.existsById(id)) {
      throw new ResourceNotFoundException("usuário não encontrado com id: " + id);
    }
    repo.deleteById(id);
  }
}
