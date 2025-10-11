package com.ecotrack.controller;

import com.ecotrack.domain.Product;
import com.ecotrack.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;

  @GetMapping public List<Product> list() { return service.list(); }
  @GetMapping("/{id}") public Product get(@PathVariable UUID id) { return service.get(id); }
  @GetMapping("/barcode/{code}") public Product byBarcode(@PathVariable String code) { return service.findByBarcode(code); }
  @GetMapping("/category/{name}") public List<Product> byCategory(@PathVariable String name) { return service.byCategory(name); }
  @GetMapping("/search") public List<Product> search(@RequestParam String q) { return service.search(q); }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public Product create(@RequestBody @Valid Product p) { return service.upsert(p); }

  @PutMapping("/{id}")
  public Product update(@PathVariable UUID id, @RequestBody @Valid Product p) {
    p.setId(id);
    return service.upsert(p);
  }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { service.delete(id); }
}
