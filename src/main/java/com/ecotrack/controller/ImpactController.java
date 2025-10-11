package com.ecotrack.controller;

import com.ecotrack.domain.ProductImpact;
import com.ecotrack.service.ImpactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/impact")
@RequiredArgsConstructor
public class ImpactController {
  private final ImpactService service;

  @GetMapping("/{productId}")
  public ProductImpact get(@PathVariable UUID productId) { return service.get(productId); }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public ProductImpact upsert(@RequestBody @Valid ProductImpact pi) { return service.upsert(pi); }
}
