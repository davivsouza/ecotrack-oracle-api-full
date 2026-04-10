package com.ecotrack.dto.mobile;

import java.math.BigDecimal;

public record NutritionalInfoResponse(
  BigDecimal calories,
  BigDecimal protein,
  BigDecimal carbs,
  BigDecimal fat,
  BigDecimal sugar,
  BigDecimal sodium,
  BigDecimal fiber
) {
}
