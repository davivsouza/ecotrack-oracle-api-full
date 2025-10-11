package com.ecotrack.service;

import com.ecotrack.domain.ProductImpact;
import com.ecotrack.repository.ProductImpactRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImpactService {
  private final ProductImpactRepository repo;

  public ProductImpact get(UUID productId) {
    return repo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Impacto não encontrado"));
  }

  public ProductImpact upsert(@Valid ProductImpact pi) {
    if (pi.getProductId() == null) throw new IllegalArgumentException("productId é obrigatório");
    pi.setUpdatedAt(OffsetDateTime.now());
    return repo.save(pi);
  }
}
