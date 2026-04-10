package com.ecotrack.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "ecotrack.security")
public class SecurityProperties {

  private String jwtSecret = "ecotrack-dev-secret";
  private long jwtExpirationMinutes = 10080;
  private List<String> adminEmails = new ArrayList<>();
}
