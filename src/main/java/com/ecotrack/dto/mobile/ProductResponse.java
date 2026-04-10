package com.ecotrack.dto.mobile;

import java.util.List;

public record ProductResponse(
  String id,
  String name,
  String brand,
  String barcode,
  String image,
  String ingredients,
  List<String> categories,
  String nutriScore,
  NutritionalInfoResponse nutritionalInfo,
  EnvironmentalImpactResponse environmentalImpact,
  Integer healthScore,
  Integer sustainabilityScore,
  List<String> alternatives,
  String dataSource,
  Integer dataCompleteness
) {
}
