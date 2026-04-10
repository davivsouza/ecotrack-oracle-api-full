package com.ecotrack.dto.mobile;

import jakarta.validation.constraints.Size;

public record HistoryUpdateRequest(
  @Size(max = 180) String note
) {
}
