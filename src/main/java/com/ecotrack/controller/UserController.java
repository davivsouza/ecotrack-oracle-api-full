package com.ecotrack.controller;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService service;

  @GetMapping public List<UserAccount> list() { return service.list(); }
  @GetMapping("/{id}") public UserAccount get(@PathVariable UUID id) { return service.get(id); }
  @GetMapping("/by-email") public UserAccount byEmail(@RequestParam String email) { return service.findByEmail(email); }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public UserAccount create(@RequestBody @Valid UserAccount u) { return service.create(u); }

  @PutMapping("/{id}")
  public UserAccount update(@PathVariable UUID id, @RequestBody @Valid UserAccount u) { return service.update(id, u); }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { service.delete(id); }
}
