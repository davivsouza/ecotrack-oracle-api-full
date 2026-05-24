package com.ecotrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OpenApiConfigTest {

  @Test
  void shouldUseHttpsRailwayPublicDomainAsSwaggerServerUrl() {
    OpenAPI openAPI = new OpenApiConfig("", "ecotrack-oracle-api-full-production.up.railway.app").api();

    assertEquals(
      "https://ecotrack-oracle-api-full-production.up.railway.app",
      openAPI.getServers().get(0).getUrl()
    );
  }

  @Test
  void shouldKeepRailwayPublicDomainProtocolWhenPresent() {
    OpenAPI openAPI = new OpenApiConfig("", "https://ecotrack-oracle-api-full-production.up.railway.app/").api();

    assertEquals(
      "https://ecotrack-oracle-api-full-production.up.railway.app",
      openAPI.getServers().get(0).getUrl()
    );
  }

  @Test
  void shouldPreferConfiguredOpenApiServerUrl() {
    OpenAPI openAPI = new OpenApiConfig("https://api.example.com/", "railway.app").api();

    assertEquals("https://api.example.com", openAPI.getServers().get(0).getUrl());
  }

  @Test
  void shouldNotSetServerUrlForLocalDefaults() {
    OpenAPI openAPI = new OpenApiConfig("", "").api();

    assertNull(openAPI.getServers());
  }
}
