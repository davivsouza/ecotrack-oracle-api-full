package com.ecotrack.representation;

import com.ecotrack.domain.Product;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Getter
public class ProductRepresentation implements RepresentationModelAssembler<Product, EntityModel<Product>> {

  @Override
  @NonNull
  public EntityModel<Product> toModel(@NonNull Product product) {
    UUID id = product.getId();
    
    EntityModel<Product> model = EntityModel.of(product);
    
    // self link
    model.add(linkTo(methodOn(com.ecotrack.controller.ProductController.class).get(id)).withSelfRel());
    
    // collection link (com paginação padrão)
    model.add(linkTo(methodOn(com.ecotrack.controller.ProductController.class).list(Pageable.unpaged())).withRel("products"));
    
    // relacionamentos
    if (product.getBarcode() != null) {
      model.add(linkTo(methodOn(com.ecotrack.controller.ProductController.class).byBarcode(product.getBarcode())).withRel("self-barcode"));
    }
    
    // links relacionados
    model.add(linkTo(methodOn(com.ecotrack.controller.ImpactController.class).get(id)).withRel("impact"));
    model.add(linkTo(methodOn(com.ecotrack.controller.NutritionController.class).list(id)).withRel("nutrition"));
    
    return model;
  }
}

