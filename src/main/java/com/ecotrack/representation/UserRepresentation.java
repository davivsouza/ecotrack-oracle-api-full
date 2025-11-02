package com.ecotrack.representation;

import com.ecotrack.domain.UserAccount;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Getter
public class UserRepresentation implements RepresentationModelAssembler<UserAccount, EntityModel<UserAccount>> {

  @Override
  @NonNull
  public EntityModel<UserAccount> toModel(@NonNull UserAccount user) {
    UUID id = user.getId();
    
    EntityModel<UserAccount> model = EntityModel.of(user);
    
    // self link
    model.add(linkTo(methodOn(com.ecotrack.controller.UserController.class).get(id)).withSelfRel());
    
    // collection link
    model.add(linkTo(methodOn(com.ecotrack.controller.UserController.class).list()).withRel("users"));
    
    // link por email
    if (user.getEmail() != null) {
      model.add(linkTo(methodOn(com.ecotrack.controller.UserController.class).byEmail(user.getEmail())).withRel("self-email"));
    }
    
    // links relacionados
    model.add(linkTo(methodOn(com.ecotrack.controller.ScanController.class).history(user.getEmail())).withRel("scan-history"));
    model.add(linkTo(methodOn(com.ecotrack.controller.ScanController.class).listFav(user.getEmail())).withRel("favorites"));
    
    return model;
  }
}

