package com.ecotrack.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "PRODUCT_IMPACT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductImpact {

  @Id
  @Column(name = "PRODUCT_ID", columnDefinition = "RAW(16)")
  private UUID productId;

  @OneToOne
  @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
  private Product product;

  @Column(name = "CO2_PER_UNIT", precision = 10, scale = 3)
  private BigDecimal co2PerUnit;

  @Column(name = "WATER_L", precision = 10, scale = 2)
  private BigDecimal waterL;

  @Column(name = "ORIGIN", length = 120)
  private String origin;

  @Column(name = "UPDATED_AT", nullable = false)
  private OffsetDateTime updatedAt;
}
