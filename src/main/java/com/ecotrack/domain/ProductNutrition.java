package com.ecotrack.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "PRODUCT_NUTRITION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductNutrition {

  @Id
  @Column(name = "ID", columnDefinition = "RAW(16)")
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @Column(name = "NUTRI_KEY", nullable = false, length = 80)
  private String nutriKey;

  @Column(name = "NUTRI_VALUE", nullable = false, length = 120)
  private String nutriValue;
}
