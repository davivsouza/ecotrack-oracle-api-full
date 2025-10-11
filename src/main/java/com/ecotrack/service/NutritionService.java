package com.ecotrack.service;

import com.ecotrack.domain.Product;
import com.ecotrack.domain.ProductNutrition;
import com.ecotrack.repository.ProductNutritionRepository;
import com.ecotrack.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NutritionService {
  private final ProductNutritionRepository repo;
  private final ProductRepository productRepo;

  public List<ProductNutrition> listByProduct(UUID productId) {
    Product p = productRepo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    return repo.findByProduct(p);
  }

  public ProductNutrition create(@Valid ProductNutrition n) {
    if (n.getId() == null) n.setId(UUID.randomUUID());
    return repo.save(n);
  }

  public void delete(UUID id) { repo.deleteById(id); }
}
