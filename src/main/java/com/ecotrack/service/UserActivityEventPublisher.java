package com.ecotrack.service;

import com.ecotrack.config.MessagingProperties;
import com.ecotrack.messaging.UserActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
public class UserActivityEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final MessagingProperties properties;

  public UserActivityEventPublisher(RabbitTemplate rabbitTemplate, MessagingProperties properties) {
    this.rabbitTemplate = rabbitTemplate;
    this.properties = properties;
  }

  public void publish(String eventType, String userId, String targetId) {
    if (!properties.isEnabled()) {
      return;
    }

    UserActivityEvent event = new UserActivityEvent(eventType, userId, targetId, OffsetDateTime.now());

    try {
      rabbitTemplate.convertAndSend(properties.getExchange(), properties.getRoutingKey(), event);
    } catch (AmqpException ex) {
      log.warn("falha ao publicar evento de atividade: {}", ex.getMessage());
    }
  }
}
