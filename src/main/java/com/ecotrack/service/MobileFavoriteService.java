package com.ecotrack.service;

import com.ecotrack.client.InternalProductClient;
import com.ecotrack.domain.Favorite;
import com.ecotrack.domain.FavoriteId;
import com.ecotrack.domain.Product;
import com.ecotrack.domain.UserAccount;
import com.ecotrack.dto.mobile.FavoriteCreateRequest;
import com.ecotrack.dto.mobile.FavoriteItemResponse;
import com.ecotrack.dto.mobile.FavoriteUpdateRequest;
import com.ecotrack.dto.mobile.ProductResponse;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.FavoriteRepository;
import com.ecotrack.security.CurrentUserService;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MobileFavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final CurrentUserService currentUserService;
  private final MobileProductService mobileProductService;
  private final InternalProductClient internalProductClient;
  private final ExternalIdCodec externalIdCodec;
  private final UserActivityEventPublisher eventPublisher;

  private final Map<String, String> favoriteNotes = new ConcurrentHashMap<>();

  public MobileFavoriteService(
    FavoriteRepository favoriteRepository,
    CurrentUserService currentUserService,
    MobileProductService mobileProductService,
    InternalProductClient internalProductClient,
    ExternalIdCodec externalIdCodec,
    UserActivityEventPublisher eventPublisher
  ) {
    this.favoriteRepository = favoriteRepository;
    this.currentUserService = currentUserService;
    this.mobileProductService = mobileProductService;
    this.internalProductClient = internalProductClient;
    this.externalIdCodec = externalIdCodec;
    this.eventPublisher = eventPublisher;
  }

  public List<FavoriteItemResponse> list() {
    UserAccount user = currentUserService.requireCurrentUser();
    return favoriteRepository.findByUser(user).stream()
      .map(this::toResponse)
      .toList();
  }

  @Transactional
  public FavoriteItemResponse create(FavoriteCreateRequest request) {
    UserAccount user = currentUserService.requireCurrentUser();

    assertProductExists(request.productId());

    Product product = mobileProductService.getEntityByExternalId(request.productId());
    favoriteRepository.findByUserAndProduct(user, product).ifPresent(existing -> {
      throw new ResourceConflictException("Produto já está nos favoritos.");
    });

    Favorite favorite = Favorite.builder()
      .id(new FavoriteId(user.getId(), product.getId()))
      .user(user)
      .product(product)
      .createdAt(OffsetDateTime.now())
      .build();

    Favorite saved = favoriteRepository.save(favorite);

    String externalId = externalIdCodec.favoriteId(saved.getProduct().getId());
    if (request.note() != null && !request.note().isBlank()) {
      favoriteNotes.put(externalId, limit(request.note(), 180));
    }

    eventPublisher.publish("FAVORITE_CREATED", externalIdCodec.userId(user.getId()), externalId);
    return toResponse(saved);
  }

  @Transactional
  public FavoriteItemResponse update(String favoriteId, FavoriteUpdateRequest request) {
    UserAccount user = currentUserService.requireCurrentUser();

    var internalProductId = externalIdCodec.parseFavoriteId(favoriteId);
    FavoriteId id = new FavoriteId(user.getId(), internalProductId);

    Favorite favorite = favoriteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado."));

    if (request.note() == null || request.note().isBlank()) {
      favoriteNotes.remove(favoriteId);
    } else {
      favoriteNotes.put(favoriteId, limit(request.note(), 180));
    }

    eventPublisher.publish("FAVORITE_UPDATED", externalIdCodec.userId(user.getId()), favoriteId);
    return toResponse(favorite);
  }

  @Transactional
  public void delete(String favoriteId) {
    UserAccount user = currentUserService.requireCurrentUser();

    var internalProductId = externalIdCodec.parseFavoriteId(favoriteId);
    FavoriteId id = new FavoriteId(user.getId(), internalProductId);

    Favorite favorite = favoriteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado."));

    favoriteRepository.delete(favorite);
    favoriteNotes.remove(favoriteId);
    eventPublisher.publish("FAVORITE_DELETED", externalIdCodec.userId(user.getId()), favoriteId);
  }

  private FavoriteItemResponse toResponse(Favorite favorite) {
    String id = externalIdCodec.favoriteId(favorite.getProduct().getId());
    String productId = externalIdCodec.productId(favorite.getProduct().getId());
    ProductResponse product = resolveProduct(favorite);

    return new FavoriteItemResponse(
      id,
      externalIdCodec.userId(favorite.getUser().getId()),
      productId,
      favorite.getCreatedAt().toString(),
      favoriteNotes.get(id),
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

  private ProductResponse resolveProduct(Favorite favorite) {
    String productId = externalIdCodec.productId(favorite.getProduct().getId());
    try {
      return internalProductClient.getById(productId);
    } catch (Exception ex) {
      return mobileProductService.toProductResponse(favorite.getProduct(), "local");
    }
  }

  private String limit(String value, int max) {
    String trimmed = value.trim();
    if (trimmed.length() <= max) {
      return trimmed;
    }
    return trimmed.substring(0, max);
  }
}
