package com.ecotrack.service;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.UserAccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserAccountRepository repo;

  public Page<UserAccount> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public List<UserAccount> list() { return repo.findAll(); }

  public UserAccount get(UUID id) {
    return repo.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com id: " + id));
  }

  public UserAccount findByEmail(String email) {
    return repo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
  }

  @Transactional
  public UserAccount create(@Valid UserAccount u) {
    // verifica se email já existe
    if (repo.findByEmail(u.getEmail()).isPresent()) {
      throw new ResourceConflictException("email já cadastrado: " + u.getEmail());
    }
    
    if (u.getId() == null) u.setId(UUID.randomUUID());
    u.setCreatedAt(OffsetDateTime.now());
    u.setUpdatedAt(OffsetDateTime.now());
    return repo.save(u);
  }

  @Transactional
  public UserAccount update(UUID id, @Valid UserAccount u) {
    UserAccount db = get(id);
    
    // verifica se email está sendo alterado e se já existe
    if (!db.getEmail().equals(u.getEmail()) && repo.findByEmail(u.getEmail()).isPresent()) {
      throw new ResourceConflictException("email já cadastrado: " + u.getEmail());
    }
    
    db.setEmail(u.getEmail());
    db.setPasswordHash(u.getPasswordHash());
    db.setDisplayName(u.getDisplayName());
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
