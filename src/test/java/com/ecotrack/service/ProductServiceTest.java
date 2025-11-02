package com.ecotrack.service;

import com.ecotrack.domain.Product;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository repository;

  @InjectMocks
  private ProductService service;

  private Product product;
  private UUID productId;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    product = Product.builder()
      .id(productId)
      .name("Test Product")
      .category("Test Category")
      .barcode("123456789")
      .kcal100g(new BigDecimal("100.0"))
      .co2PerUnit(new BigDecimal("1.5"))
      .build();
  }

  @Test
  void shouldGetProductById() {
    when(repository.findById(productId)).thenReturn(Optional.of(product));

    Product result = service.get(productId);

    assertNotNull(result);
    assertEquals(productId, result.getId());
    assertEquals("Test Product", result.getName());
    verify(repository, times(1)).findById(productId);
  }

  @Test
  void shouldThrowExceptionWhenProductNotFound() {
    when(repository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.get(productId));
    verify(repository, times(1)).findById(productId);
  }

  @Test
  void shouldFindProductByBarcode() {
    when(repository.findByBarcode("123456789")).thenReturn(Optional.of(product));

    Product result = service.findByBarcode("123456789");

    assertNotNull(result);
    assertEquals("123456789", result.getBarcode());
    verify(repository, times(1)).findByBarcode("123456789");
  }

  @Test
  void shouldThrowExceptionWhenBarcodeNotFound() {
    when(repository.findByBarcode("999999")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findByBarcode("999999"));
    verify(repository, times(1)).findByBarcode("999999");
  }

  @Test
  void shouldCreateProductWithNewBarcode() {
    Product newProduct = Product.builder()
      .name("New Product")
      .category("Category")
      .barcode("987654321")
      .build();

    when(repository.findByBarcode("987654321")).thenReturn(Optional.empty());
    when(repository.save(any(Product.class))).thenReturn(newProduct);

    Product result = service.upsert(newProduct);

    assertNotNull(result);
    assertNotNull(result.getId());
    verify(repository, times(1)).findByBarcode("987654321");
    verify(repository, times(1)).save(any(Product.class));
  }

  @Test
  void shouldThrowExceptionWhenBarcodeAlreadyExists() {
    Product newProduct = Product.builder()
      .name("New Product")
      .category("Category")
      .barcode("123456789")
      .build();

    when(repository.findByBarcode("123456789")).thenReturn(Optional.of(product));

    assertThrows(ResourceConflictException.class, () -> service.upsert(newProduct));
    verify(repository, never()).save(any());
  }

  @Test
  void shouldDeleteProduct() {
    when(repository.existsById(productId)).thenReturn(true);
    doNothing().when(repository).deleteById(productId);

    service.delete(productId);

    verify(repository, times(1)).existsById(productId);
    verify(repository, times(1)).deleteById(productId);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentProduct() {
    when(repository.existsById(productId)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> service.delete(productId));
    verify(repository, never()).deleteById(productId);
  }
}

