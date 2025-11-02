package com.ecotrack.service;

import com.ecotrack.domain.Product;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository repo;

  public Page<Product> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public List<Product> list() { return repo.findAll(); }

  public Product get(UUID id) {
    return repo.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("produto não encontrado com id: " + id));
  }

  public Product findByBarcode(String barcode) {
    return repo.findByBarcode(barcode)
      .orElseThrow(() -> new ResourceNotFoundException("produto não encontrado com código de barras: " + barcode));
  }

  public Page<Product> byCategory(String category, Pageable pageable) {
    return repo.findByCategoryIgnoreCase(category, pageable);
  }

  public List<Product> byCategory(String category) { 
    return repo.findByCategoryIgnoreCase(category); 
  }

  public Page<Product> search(String term, Pageable pageable) {
    if (term == null || term.trim().isEmpty()) {
      return Page.empty(pageable);
    }
    return repo.searchByName(term.trim(), pageable);
  }

  public List<Product> search(String term) { 
    if (term == null || term.trim().isEmpty()) {
      return List.of();
    }
    return repo.searchByName(term.trim()); 
  }

  @Transactional
  public Product upsert(@Valid Product p) {
    // se é criação, verifica se código de barras já existe
    if (p.getId() == null) {
      if (p.getBarcode() != null && repo.findByBarcode(p.getBarcode()).isPresent()) {
        throw new ResourceConflictException("produto com código de barras já cadastrado: " + p.getBarcode());
      }
      p.setId(UUID.randomUUID());
    } else {
      // se é atualização, verifica se código de barras está sendo alterado
      Product existing = get(p.getId());
      if (p.getBarcode() != null && !existing.getBarcode().equals(p.getBarcode())) {
        if (repo.findByBarcode(p.getBarcode()).isPresent()) {
          throw new ResourceConflictException("produto com código de barras já cadastrado: " + p.getBarcode());
        }
      }
    }
    return repo.save(p);
  }

  @Transactional
  public void delete(UUID id) {
    if (!repo.existsById(id)) {
      throw new ResourceNotFoundException("produto não encontrado com id: " + id);
    }
    repo.deleteById(id);
  }
}
