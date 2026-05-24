package com.ecotrack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class OpenApiConfig {
  private final String configuredServerUrl;
  private final String railwayPublicDomain;

  public OpenApiConfig(
    @Value("${OPENAPI_SERVER_URL:${openapi.server-url:}}") String configuredServerUrl,
    @Value("${RAILWAY_PUBLIC_DOMAIN:}") String railwayPublicDomain
  ) {
    this.configuredServerUrl = configuredServerUrl;
    this.railwayPublicDomain = railwayPublicDomain;
  }

  @Bean
  public OpenAPI api() {
    String bearerScheme = "bearerAuth";

    OpenAPI openAPI = new OpenAPI()
      .info(new Info()
        .title("EcoTrack Oracle API")
        .version("0.1.0")
        .description("API EcoTrack (Sprint 3 Java Advanced)"))
      .addSecurityItem(new SecurityRequirement().addList(bearerScheme))
      .components(new Components()
        .addSecuritySchemes(bearerScheme, new SecurityScheme()
          .name(bearerScheme)
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")));

    publicServerUrl().ifPresent(url -> openAPI.addServersItem(new Server().url(url)));

    return openAPI;
  }

  private Optional<String> publicServerUrl() {
    if (configuredServerUrl != null && !configuredServerUrl.isBlank()) {
      return Optional.of(stripTrailingSlash(configuredServerUrl));
    }

    if (railwayPublicDomain != null && !railwayPublicDomain.isBlank()) {
      String normalizedDomain = stripTrailingSlash(railwayPublicDomain);
      if (normalizedDomain.startsWith("http://") || normalizedDomain.startsWith("https://")) {
        return Optional.of(normalizedDomain);
      }

      return Optional.of("https://" + normalizedDomain);
    }

    return Optional.empty();
  }

  private String stripTrailingSlash(String value) {
    return value.strip().replaceAll("/+$", "");
  }
}
