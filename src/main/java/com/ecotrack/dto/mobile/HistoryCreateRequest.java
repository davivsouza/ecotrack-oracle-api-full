package com.ecotrack.dto.mobile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HistoryCreateRequest(
  @NotBlank String productId,
  @Size(max = 180) String note
) {
}
