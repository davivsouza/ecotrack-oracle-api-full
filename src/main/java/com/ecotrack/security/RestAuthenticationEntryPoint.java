package com.ecotrack.security;

import com.ecotrack.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException authException
  ) throws IOException, ServletException {
    String authorization = request.getHeader("Authorization");
    String message = (authorization == null || authorization.isBlank()) ? "Token ausente." : "Token inválido.";

    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpServletResponse.SC_UNAUTHORIZED)
      .error("Unauthorized")
      .message(message)
      .path(request.getRequestURI())
      .build();

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getOutputStream(), error);
  }
}
