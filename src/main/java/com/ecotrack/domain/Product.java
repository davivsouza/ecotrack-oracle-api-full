package com.ecotrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "PRODUCTS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

  @Id
  @Column(name = "ID", columnDefinition = "RAW(16)")
  private UUID id; // GUID gerado no .NET; se null, API pode gerar para testes

  @NotBlank
  @Column(name = "NAME", length = 200, nullable = false)
  private String name;

  @NotBlank
  @Column(name = "CATEGORY", length = 120, nullable = false)
  private String category;

  @Column(name = "KCAL_100G", precision = 10, scale = 2)
  private BigDecimal kcal100g;

  @Column(name = "CO2_PER_UNIT", precision = 10, scale = 3)
  private BigDecimal co2PerUnit;

  @Column(name = "BARCODE", length = 64)
  private String barcode;
}
