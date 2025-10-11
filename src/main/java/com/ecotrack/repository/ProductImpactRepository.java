package com.ecotrack.repository;

import com.ecotrack.domain.ProductImpact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductImpactRepository extends JpaRepository<ProductImpact, UUID> { }
