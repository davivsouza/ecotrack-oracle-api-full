package com.ecotrack.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI api() {
    return new OpenAPI().info(new Info()
      .title("EcoTrack Oracle API")
      .version("0.1.0")
      .description("API EcoTrack (Oracle RAW(16) UUID) — sem Flyway"));
  }
}
