package com.ecotrack.controller;

import com.ecotrack.domain.UserAccount;
import com.ecotrack.representation.UserRepresentation;
import com.ecotrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService service;
  private final UserRepresentation userRepresentation;

  @GetMapping 
  public CollectionModel<EntityModel<UserAccount>> list() {
    List<EntityModel<UserAccount>> users = service.list().stream()
      .map(userRepresentation::toModel)
      .collect(Collectors.toList());
    
    return CollectionModel.of(users,
      linkTo(methodOn(UserController.class).list()).withSelfRel());
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
