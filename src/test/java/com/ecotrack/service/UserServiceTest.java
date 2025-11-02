package com.ecotrack.service;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserAccountRepository repository;

  @InjectMocks
  private UserService service;

  private UserAccount user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserAccount.builder()
      .id(userId)
      .email("test@example.com")
      .passwordHash("hashedPassword")
      .displayName("Test User")
      .createdAt(OffsetDateTime.now())
      .updatedAt(OffsetDateTime.now())
      .build();
  }

  @Test
  void shouldGetUserById() {
    when(repository.findById(userId)).thenReturn(Optional.of(user));

    UserAccount result = service.get(userId);

    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("test@example.com", result.getEmail());
    verify(repository, times(1)).findById(userId);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    when(repository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.get(userId));
    verify(repository, times(1)).findById(userId);
  }

  @Test
  void shouldCreateUserWithNewEmail() {
    UserAccount newUser = UserAccount.builder()
      .email("new@example.com")
      .passwordHash("password")
      .displayName("New User")
      .build();

    when(repository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(repository.save(any(UserAccount.class))).thenReturn(newUser);

    UserAccount result = service.create(newUser);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedAt());
    verify(repository, times(1)).findByEmail("new@example.com");
    verify(repository, times(1)).save(any(UserAccount.class));
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    UserAccount newUser = UserAccount.builder()
      .email("test@example.com")
      .passwordHash("password")
      .build();

    when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    assertThrows(ResourceConflictException.class, () -> service.create(newUser));
    verify(repository, never()).save(any());
  }

  @Test
  void shouldUpdateUser() {
    UserAccount updatedData = UserAccount.builder()
      .email("test@example.com")
      .passwordHash("newPassword")
      .displayName("Updated Name")
      .build();

    when(repository.findById(userId)).thenReturn(Optional.of(user));
    when(repository.save(any(UserAccount.class))).thenReturn(user);

    UserAccount result = service.update(userId, updatedData);

    assertNotNull(result);
    verify(repository, times(1)).findById(userId);
    verify(repository, times(1)).save(any(UserAccount.class));
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExistsOnUpdate() {
    UserAccount updatedData = UserAccount.builder()
      .email("existing@example.com")
      .passwordHash("password")
      .build();

    UserAccount existingUser = UserAccount.builder()
      .id(UUID.randomUUID())
      .email("existing@example.com")
      .build();

    when(repository.findById(userId)).thenReturn(Optional.of(user));
    when(repository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

    assertThrows(ResourceConflictException.class, () -> service.update(userId, updatedData));
    verify(repository, never()).save(any());
  }

  @Test
  void shouldDeleteUser() {
    when(repository.existsById(userId)).thenReturn(true);
    doNothing().when(repository).deleteById(userId);

    service.delete(userId);

    verify(repository, times(1)).existsById(userId);
    verify(repository, times(1)).deleteById(userId);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentUser() {
    when(repository.existsById(userId)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> service.delete(userId));
    verify(repository, never()).deleteById(userId);
  }
}

