package com.ecotrack.repository;

import com.ecotrack.domain.ProductNutrition;
import com.ecotrack.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductNutritionRepository extends JpaRepository<ProductNutrition, UUID> {
  List<ProductNutrition> findByProduct(Product product);
}
