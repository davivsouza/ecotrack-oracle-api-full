package com.ecotrack.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "ecotrack.messaging.enabled", havingValue = "true")
public class UserActivityEventListener {

  @RabbitListener(queues = "${ecotrack.messaging.queue}")
  public void onMessage(UserActivityEvent event) {
    log.info("evento assíncrono processado: type={} user={} target={} at={}",
      event.eventType(),
      event.userId(),
      event.targetId(),
      event.occurredAt());
  }
}
