package com.ecotrack.dto.mobile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FavoriteCreateRequest(
  @NotBlank String productId,
  @Size(max = 180) String note
) {
}
