package com.ecotrack.dto.mobile;

public record HistoryItemResponse(
  String id,
  String userId,
  String productId,
  String scannedAt,
  String note,
  ProductResponse product
) {
}
