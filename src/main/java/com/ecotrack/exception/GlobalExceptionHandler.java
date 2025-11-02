package com.ecotrack.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleEntityNotFound(RuntimeException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("recurso não encontrado: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.NOT_FOUND.value())
      .error("Not Found")
      .message(ex.getMessage())
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ResourceConflictException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("conflito de recurso: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.CONFLICT.value())
      .error("Conflict")
      .message(ex.getMessage())
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("erro de status: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(ex.getStatusCode().value())
      .error(ex.getStatusCode().toString())
      .message(ex.getReason())
      .path(getPath(request))
      .build();
    return ResponseEntity.status(ex.getStatusCode()).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("erro de validação: {}", ex.getMessage());
    List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(this::mapToValidationError)
      .collect(Collectors.toList());

    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Bad Request")
      .message("erros de validação encontrados")
      .path(getPath(request))
      .validationErrors(errors)
      .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("violação de constraint: {}", ex.getMessage());
    List<ErrorResponse.ValidationError> errors = ex.getConstraintViolations()
      .stream()
      .map(this::mapConstraintViolation)
      .collect(Collectors.toList());

    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Bad Request")
      .message("violação de constraints")
      .path(getPath(request))
      .validationErrors(errors)
      .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("violação de integridade: {}", ex.getMessage());
    String message = "violação de integridade de dados";
    if (ex.getMessage() != null && ex.getMessage().contains("EMAIL")) {
      message = "email já cadastrado";
    }
    
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.CONFLICT.value())
      .error("Conflict")
      .message(message)
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("erro de tipo: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Bad Request")
      .message("tipo de parâmetro inválido: " + ex.getName())
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("erro ao ler requisição: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Bad Request")
      .message("corpo da requisição inválido ou mal formatado")
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, org.springframework.web.context.request.WebRequest request) {
    log.error("método não suportado: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.METHOD_NOT_ALLOWED.value())
      .error("Method Not Allowed")
      .message("método HTTP não suportado: " + ex.getMethod())
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, org.springframework.web.context.request.WebRequest request) {
    log.error("erro inesperado: ", ex);
    ErrorResponse error = ErrorResponse.builder()
      .timestamp(OffsetDateTime.now())
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .error("Internal Server Error")
      .message("erro interno do servidor")
      .path(getPath(request))
      .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private ErrorResponse.ValidationError mapToValidationError(FieldError fieldError) {
    return ErrorResponse.ValidationError.builder()
      .field(fieldError.getField())
      .message(fieldError.getDefaultMessage())
      .rejectedValue(fieldError.getRejectedValue())
      .build();
  }

  private ErrorResponse.ValidationError mapConstraintViolation(ConstraintViolation<?> violation) {
    return ErrorResponse.ValidationError.builder()
      .field(violation.getPropertyPath().toString())
      .message(violation.getMessage())
      .rejectedValue(violation.getInvalidValue())
      .build();
  }

  private String getPath(org.springframework.web.context.request.WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}

