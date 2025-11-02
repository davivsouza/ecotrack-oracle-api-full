package com.ecotrack.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private OffsetDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private List<ValidationError> validationErrors;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ValidationError {
    private String field;
    private String message;
    private Object rejectedValue;
  }
}

