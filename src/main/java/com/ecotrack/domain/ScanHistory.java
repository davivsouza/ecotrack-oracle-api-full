package com.ecotrack.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "SCAN_HISTORY")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScanHistory {

  @Id
  @Column(name = "ID", columnDefinition = "RAW(16)")
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "USER_ID")
  private UserAccount user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @Column(name = "SCANNED_AT", nullable = false)
  private OffsetDateTime scannedAt;

  @Column(name = "SOURCE", length = 60)
  private String source; // MOBILE / WEB
}
