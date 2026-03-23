package com.ecotrack.service;

import com.ecotrack.domain.Favorite;
import com.ecotrack.domain.FavoriteId;
import com.ecotrack.domain.Product;
import com.ecotrack.domain.ScanHistory;
import com.ecotrack.domain.UserAccount;
import com.ecotrack.exception.ResourceConflictException;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.FavoriteRepository;
import com.ecotrack.repository.ProductRepository;
import com.ecotrack.repository.ScanHistoryRepository;
import com.ecotrack.repository.UserAccountRepository;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScanService {
  private final ProductRepository productRepo;
  private final UserAccountRepository userRepo;
  private final ScanHistoryRepository scanRepo;
  private final FavoriteRepository favRepo;

  @Transactional
  public ScanHistory recordScan(@NotBlank String email, @NotBlank String barcode) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
    Product product = productRepo.findByBarcode(barcode)
      .orElseThrow(() -> new ResourceNotFoundException("produto não encontrado com código de barras: " + barcode));

    ScanHistory scan = ScanHistory.builder()
      .id(UUID.randomUUID())
      .product(product)
      .user(user)
      .scannedAt(OffsetDateTime.now())
      .source("WEB")
      .build();
    return scanRepo.save(scan);
  }

  public List<ScanHistory> listHistory(String email) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
    return scanRepo.findByUserOrderByScannedAtDesc(user);
  }

  @Transactional
  public Favorite addFavorite(String email, UUID productId) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
    Product product = productRepo.findById(productId)
      .orElseThrow(() -> new ResourceNotFoundException("produto não encontrado com id: " + productId));

    FavoriteId favId = new FavoriteId(user.getId(), product.getId());
    if (favRepo.existsById(favId)) {
      throw new ResourceConflictException("produto já está nos favoritos");
    }

    Favorite fav = Favorite.builder()
      .id(favId)
      .user(user)
      .product(product)
      .createdAt(OffsetDateTime.now())
      .build();
    return favRepo.save(fav);
  }

  @Transactional
  public void removeFavorite(String email, UUID productId) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
    FavoriteId favId = new FavoriteId(user.getId(), productId);
    if (!favRepo.existsById(favId)) {
      throw new ResourceNotFoundException("favorito não encontrado");
    }
    favRepo.deleteById(favId);
  }

  public List<Favorite> listFavorites(String email) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
    return favRepo.findByUser(user);
  }
}
