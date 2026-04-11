package com.ecotrack.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitMessagingConfig {

  @Bean
  public MessageConverter rabbitMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  @ConditionalOnProperty(name = "ecotrack.messaging.enabled", havingValue = "true")
  public TopicExchange ecoTrackExchange(MessagingProperties properties) {
    return new TopicExchange(properties.getExchange());
  }

  @Bean
  @ConditionalOnProperty(name = "ecotrack.messaging.enabled", havingValue = "true")
  public Queue ecoTrackQueue(MessagingProperties properties) {
    return new Queue(properties.getQueue(), true);
  }

  @Bean
  @ConditionalOnProperty(name = "ecotrack.messaging.enabled", havingValue = "true")
  public Binding ecoTrackBinding(Queue ecoTrackQueue, TopicExchange ecoTrackExchange, MessagingProperties properties) {
    return BindingBuilder.bind(ecoTrackQueue).to(ecoTrackExchange).with(properties.getRoutingKey());
  }
}
