package com.ecotrack.service;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserAccountRepository repo;

  public List<UserAccount> list() { return repo.findAll(); }

  public UserAccount get(UUID id) {
    return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
  }

  public UserAccount findByEmail(String email) {
    return repo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
  }

  public UserAccount create(@Valid UserAccount u) {
    if (u.getId() == null) u.setId(UUID.randomUUID());
    u.setCreatedAt(OffsetDateTime.now());
    u.setUpdatedAt(OffsetDateTime.now());
    return repo.save(u);
  }

  public UserAccount update(UUID id, @Valid UserAccount u) {
    UserAccount db = get(id);
    db.setEmail(u.getEmail());
    db.setPasswordHash(u.getPasswordHash());
    db.setDisplayName(u.getDisplayName());
    db.setUpdatedAt(OffsetDateTime.now());
    return repo.save(db);
  }

  public void delete(UUID id) { repo.deleteById(id); }
}
