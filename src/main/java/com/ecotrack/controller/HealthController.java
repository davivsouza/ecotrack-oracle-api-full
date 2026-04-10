package com.ecotrack.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

  private final JdbcTemplate jdbcTemplate;

  public HealthController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", "ok");

    try {
      jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
      response.put("database", "up");
    } catch (Exception ex) {
      response.put("database", "down");
    }

    return response;
  }
}
