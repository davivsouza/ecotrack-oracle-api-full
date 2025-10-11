package com.ecotrack.service;

import com.ecotrack.domain.Product;
import com.ecotrack.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository repo;

  public List<Product> list() { return repo.findAll(); }

  public Product get(UUID id) {
    return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
  }

  public Product findByBarcode(String barcode) {
    return repo.findByBarcode(barcode).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
  }

  public List<Product> byCategory(String category) { return repo.findByCategoryIgnoreCase(category); }

  public List<Product> search(String term) { return repo.searchByName(term); }

  public Product upsert(@Valid Product p) {
    if (p.getId() == null) p.setId(UUID.randomUUID()); // ajuda em testes locais
    return repo.save(p);
  }

  public void delete(UUID id) { repo.deleteById(id); }
}
