package com.ecotrack.service;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class ExternalIdCodec {

  public String userId(UUID id) {
    return encode("user", id);
  }

  public String productId(UUID id) {
    return encode("prod", id);
  }

  public String historyId(UUID id) {
    return encode("history", id);
  }

  public String favoriteId(UUID productId) {
    return encode("favorite", productId);
  }

  public UUID parseUserId(String id) {
    return decode(id, "user");
  }

  public UUID parseProductId(String id) {
    return decode(id, "prod");
  }

  public UUID parseHistoryId(String id) {
    return decode(id, "history");
  }

  public UUID parseFavoriteId(String id) {
    return decode(id, "favorite");
  }

  private String encode(String prefix, UUID id) {
    return prefix + "-" + id.toString().replace("-", "");
  }

  private UUID decode(String rawId, String prefix) {
    if (rawId == null || rawId.isBlank()) {
      throw new ResponseStatusException(BAD_REQUEST, "id inválido");
    }

    try {
      return UUID.fromString(rawId);
    } catch (IllegalArgumentException ignored) {
      // tenta parsing com prefixo.
    }

    String normalized = rawId.trim().toLowerCase();
    String expectedPrefix = prefix + "-";
    if (!normalized.startsWith(expectedPrefix)) {
      throw new ResponseStatusException(BAD_REQUEST, "id inválido");
    }

    String compact = normalized.substring(expectedPrefix.length());
    if (compact.length() != 32) {
      throw new ResponseStatusException(BAD_REQUEST, "id inválido");
    }

    String uuid = compact.substring(0, 8) + "-"
      + compact.substring(8, 12) + "-"
      + compact.substring(12, 16) + "-"
      + compact.substring(16, 20) + "-"
      + compact.substring(20);

    try {
      return UUID.fromString(uuid);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(BAD_REQUEST, "id inválido");
    }
  }
}
