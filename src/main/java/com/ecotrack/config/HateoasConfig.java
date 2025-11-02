package com.ecotrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;

@Configuration
public class HateoasConfig {

  @Bean
  public LinkRelationProvider linkRelationProvider() {
    return new DefaultLinkRelationProvider();
  }
}

