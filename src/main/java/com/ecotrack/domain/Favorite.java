package com.ecotrack.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "FAVORITES")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite {

  @EmbeddedId
  private FavoriteId id;

  @ManyToOne(optional = false)
  @MapsId("userId")
  @JoinColumn(name = "USER_ID")
  private UserAccount user;

  @ManyToOne(optional = false)
  @MapsId("productId")
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @Column(name = "CREATED_AT", nullable = false)
  private OffsetDateTime createdAt;
}
