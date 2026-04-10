package com.ecotrack.dto.mobile;

public record FavoriteItemResponse(
  String id,
  String userId,
  String productId,
  String createdAt,
  String note,
  ProductResponse product
) {
}
