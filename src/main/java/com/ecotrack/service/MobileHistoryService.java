package com.ecotrack.service;

import com.ecotrack.client.InternalProductClient;
import com.ecotrack.domain.ScanHistory;
import com.ecotrack.domain.UserAccount;
import com.ecotrack.dto.mobile.HistoryCreateRequest;
import com.ecotrack.dto.mobile.HistoryItemResponse;
import com.ecotrack.dto.mobile.HistoryUpdateRequest;
import com.ecotrack.dto.mobile.ProductResponse;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ScanHistoryRepository;
import com.ecotrack.security.CurrentUserService;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MobileHistoryService {

  private final ScanHistoryRepository scanHistoryRepository;
  private final CurrentUserService currentUserService;
  private final MobileProductService mobileProductService;
  private final InternalProductClient internalProductClient;
  private final ExternalIdCodec externalIdCodec;
  private final UserActivityEventPublisher eventPublisher;

  public MobileHistoryService(
    ScanHistoryRepository scanHistoryRepository,
    CurrentUserService currentUserService,
    MobileProductService mobileProductService,
    InternalProductClient internalProductClient,
    ExternalIdCodec externalIdCodec,
    UserActivityEventPublisher eventPublisher
  ) {
    this.scanHistoryRepository = scanHistoryRepository;
    this.currentUserService = currentUserService;
    this.mobileProductService = mobileProductService;
    this.internalProductClient = internalProductClient;
    this.externalIdCodec = externalIdCodec;
    this.eventPublisher = eventPublisher;
  }

  public List<HistoryItemResponse> list() {
    UserAccount user = currentUserService.requireCurrentUser();
    return scanHistoryRepository.findByUserOrderByScannedAtDesc(user).stream()
      .map(this::toResponse)
      .toList();
  }

  @Transactional
  public HistoryItemResponse create(HistoryCreateRequest request) {
    UserAccount user = currentUserService.requireCurrentUser();

    // validação síncrona via Feign na própria API (Sprint 3 requirement).
    assertProductExists(request.productId());

    ScanHistory history = ScanHistory.builder()
      .id(UUID.randomUUID())
      .user(user)
      .product(mobileProductService.getEntityByExternalId(request.productId()))
      .scannedAt(OffsetDateTime.now())
      .source(toStoredNote(request.note()))
      .build();

    ScanHistory saved = scanHistoryRepository.save(history);
    eventPublisher.publish("HISTORY_CREATED", externalIdCodec.userId(user.getId()), externalIdCodec.historyId(saved.getId()));
    return toResponse(saved);
  }

  @Transactional
  public HistoryItemResponse update(String historyId, HistoryUpdateRequest request) {
    UserAccount user = currentUserService.requireCurrentUser();
    UUID internalId = externalIdCodec.parseHistoryId(historyId);

    ScanHistory history = scanHistoryRepository.findByIdAndUser(internalId, user)
      .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado."));

    history.setSource(toStoredNote(request.note()));
    ScanHistory saved = scanHistoryRepository.save(history);
    eventPublisher.publish("HISTORY_UPDATED", externalIdCodec.userId(user.getId()), externalIdCodec.historyId(saved.getId()));
    return toResponse(saved);
  }

  @Transactional
  public void delete(String historyId) {
    UserAccount user = currentUserService.requireCurrentUser();
    UUID internalId = externalIdCodec.parseHistoryId(historyId);

    ScanHistory history = scanHistoryRepository.findByIdAndUser(internalId, user)
      .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado."));

    scanHistoryRepository.delete(history);
    eventPublisher.publish("HISTORY_DELETED", externalIdCodec.userId(user.getId()), externalIdCodec.historyId(internalId));
  }

  private HistoryItemResponse toResponse(ScanHistory history) {
    ProductResponse product = resolveProduct(history);

    return new HistoryItemResponse(
      externalIdCodec.historyId(history.getId()),
      externalIdCodec.userId(history.getUser().getId()),
      externalIdCodec.productId(history.getProduct().getId()),
      history.getScannedAt().toString(),
      toReadNote(history.getSource()),
      product
    );
  }

  private void assertProductExists(String productId) {
    try {
      internalProductClient.getById(productId);
    } catch (FeignException.NotFound ex) {
      throw new ResourceNotFoundException("Produto não encontrado.");
    }
  }

  private ProductResponse resolveProduct(ScanHistory history) {
    String productId = externalIdCodec.productId(history.getProduct().getId());
    try {
      return internalProductClient.getById(productId);
    } catch (Exception ex) {
      return mobileProductService.toProductResponse(history.getProduct(), "local");
    }
  }

  private String toStoredNote(String note) {
    if (note == null || note.isBlank()) {
      return "MOBILE";
    }

    String normalized = note.trim();
    if (normalized.length() > 60) {
      return normalized.substring(0, 60);
    }
    return normalized;
  }

  private String toReadNote(String source) {
    if (source == null || source.isBlank() || "MOBILE".equalsIgnoreCase(source) || "WEB".equalsIgnoreCase(source)) {
      return null;
    }
    return source;
  }
}
