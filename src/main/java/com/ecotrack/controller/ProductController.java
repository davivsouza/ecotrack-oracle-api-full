package com.ecotrack.controller;

import com.ecotrack.domain.Product;
import com.ecotrack.representation.ProductRepresentation;
import com.ecotrack.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;
  private final ProductRepresentation productRepresentation;
  private final PagedResourcesAssembler<Product> pagedAssembler;

  @GetMapping 
  public PagedModel<EntityModel<Product>> list(Pageable pageable) {
    Page<Product> productsPage = service.list(pageable);
    return pagedAssembler.toModel(productsPage, productRepresentation);
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
  public CollectionModel<EntityModel<Product>> byCategory(@PathVariable String name, Pageable pageable) {
    Page<Product> productsPage = service.byCategory(name, pageable);
    PagedModel<EntityModel<Product>> pagedModel = pagedAssembler.toModel(productsPage, productRepresentation);
    return CollectionModel.of(pagedModel.getContent(),
      linkTo(methodOn(ProductController.class).byCategory(name, pageable)).withSelfRel(),
      linkTo(methodOn(ProductController.class).list(pageable)).withRel("products"));
  }

  @GetMapping("/search") 
  public CollectionModel<EntityModel<Product>> search(@RequestParam String q, Pageable pageable) {
    Page<Product> productsPage = service.search(q, pageable);
    PagedModel<EntityModel<Product>> pagedModel = pagedAssembler.toModel(productsPage, productRepresentation);
    return CollectionModel.of(pagedModel.getContent(),
      linkTo(methodOn(ProductController.class).search(q, pageable)).withSelfRel(),
      linkTo(methodOn(ProductController.class).list(pageable)).withRel("products"));
  }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<Product> create(@RequestBody @Valid Product p) {
    Product created = service.upsert(p);
    return productRepresentation.toModel(created);
  }

  @PutMapping("/{id}")
  public EntityModel<Product> update(@PathVariable UUID id, @RequestBody @Valid Product p) {
    Product existing = service.get(id);
    p.setId(existing.getId());
    Product updated = service.upsert(p);
    return productRepresentation.toModel(updated);
  }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { service.delete(id); }
}
