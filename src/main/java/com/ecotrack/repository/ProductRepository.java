package com.ecotrack.repository;

import com.ecotrack.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  Optional<Product> findByBarcode(String barcode);
  List<Product> findByCategoryIgnoreCase(String category);

  @Query("select p from Product p where upper(p.name) like upper(concat('%', ?1, '%'))") 
  List<Product> searchByName(String term);
}
