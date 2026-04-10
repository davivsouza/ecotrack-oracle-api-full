package com.ecotrack.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ecotrack.messaging")
public class MessagingProperties {

  private boolean enabled = false;
  private String exchange = "ecotrack.events";
  private String queue = "ecotrack.activity.queue";
  private String routingKey = "ecotrack.activity";
}
