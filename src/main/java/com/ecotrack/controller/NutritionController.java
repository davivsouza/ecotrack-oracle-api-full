package com.ecotrack.controller;

import com.ecotrack.domain.ProductNutrition;
import com.ecotrack.service.NutritionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {
  private final NutritionService service;

  @GetMapping("/{productId}")
  public List<ProductNutrition> list(@PathVariable UUID productId) { return service.listByProduct(productId); }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public ProductNutrition create(@RequestBody @Valid ProductNutrition n) { return service.create(n); }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { service.delete(id); }
}
