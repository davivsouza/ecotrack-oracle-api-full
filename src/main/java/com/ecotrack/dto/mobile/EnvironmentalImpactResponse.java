package com.ecotrack.dto.mobile;

import java.math.BigDecimal;

public record EnvironmentalImpactResponse(
  BigDecimal carbonFootprint,
  BigDecimal waterUsage,
  String packagingType,
  Integer sustainabilityScore,
  String environmentalImpactLevel,
  String ecoScoreGrade
) {
}
