package com.ecotrack.messaging;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record UserActivityEvent(
  String eventType,
  String userId,
  String targetId,
  OffsetDateTime occurredAt
) implements Serializable {
}
