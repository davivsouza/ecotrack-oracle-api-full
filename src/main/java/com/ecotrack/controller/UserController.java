package com.ecotrack.controller;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.representation.UserRepresentation;
import com.ecotrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService service;
  private final UserRepresentation userRepresentation;
  private final PagedResourcesAssembler<UserAccount> pagedAssembler;

  @GetMapping 
  public PagedModel<EntityModel<UserAccount>> list(Pageable pageable) {
    Page<UserAccount> usersPage = service.list(pageable);
    return pagedAssembler.toModel(usersPage, userRepresentation);
  }

  @GetMapping("/{id}") 
  public EntityModel<UserAccount> get(@PathVariable UUID id) {
    return userRepresentation.toModel(service.get(id));
  }

  @GetMapping("/by-email") 
  public EntityModel<UserAccount> byEmail(@RequestParam String email) {
    return userRepresentation.toModel(service.findByEmail(email));
  }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<UserAccount> create(@RequestBody @Valid UserAccount u) {
    UserAccount created = service.create(u);
    return userRepresentation.toModel(created);
  }

  @PutMapping("/{id}")
  public EntityModel<UserAccount> update(@PathVariable UUID id, @RequestBody @Valid UserAccount u) {
    UserAccount updated = service.update(id, u);
    return userRepresentation.toModel(updated);
  }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { service.delete(id); }
}
