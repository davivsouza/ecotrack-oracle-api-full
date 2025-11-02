package com.ecotrack.controller;

import com.ecotrack.domain.Product;
import com.ecotrack.representation.ProductRepresentation;
import com.ecotrack.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;
  private final ProductRepresentation productRepresentation;

  @GetMapping 
  public CollectionModel<EntityModel<Product>> list() {
    List<EntityModel<Product>> products = service.list().stream()
      .map(productRepresentation::toModel)
      .collect(Collectors.toList());
    
    return CollectionModel.of(products,
      linkTo(methodOn(ProductController.class).list()).withSelfRel());
  }

  @GetMapping("/{id}") 
  public EntityModel<Product> get(@PathVariable UUID id) {
    return productRepresentation.toModel(service.get(id));
  }

  @GetMapping("/barcode/{code}") 
  public EntityModel<Product> byBarcode(@PathVariable String code) {
    return productRepresentation.toModel(service.findByBarcode(code));
  }

  @GetMapping("/category/{name}") 
  public CollectionModel<EntityModel<Product>> byCategory(@PathVariable String name) {
    List<EntityModel<Product>> products = service.byCategory(name).stream()
      .map(productRepresentation::toModel)
      .collect(Collectors.toList());
    
    return CollectionModel.of(products,
      linkTo(methodOn(ProductController.class).byCategory(name)).withSelfRel(),
      linkTo(methodOn(ProductController.class).list()).withRel("products"));
  }

  @GetMapping("/search") 
  public CollectionModel<EntityModel<Product>> search(@RequestParam String q) {
    List<EntityModel<Product>> products = service.search(q).stream()
      .map(productRepresentation::toModel)
      .collect(Collectors.toList());
    
    return CollectionModel.of(products,
      linkTo(methodOn(ProductController.class).search(q)).withSelfRel(),
      linkTo(methodOn(ProductController.class).list()).withRel("products"));
  }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<Product> create(@RequestBody @Valid Product p) {
    Product created = service.upsert(p);
    return productRepresentation.toModel(created);
  }

  @PutMapping("/{id}")
  public EntityModel<Product> update(@PathVariable UUID id, @RequestBody @Valid Product p) {
    p.setId(id);
    Product updated = service.upsert(p);
    return productRepresentation.toModel(updated);
  }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { service.delete(id); }
}
