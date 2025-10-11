package com.ecotrack.service;

import com.ecotrack.domain.*;
import com.ecotrack.repository.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScanService {
  private final ProductRepository productRepo;
  private final UserAccountRepository userRepo;
  private final ScanHistoryRepository scanRepo;
  private final FavoriteRepository favRepo;

  public ScanHistory recordScan(@NotBlank String email, @NotBlank String barcode) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    Product product = productRepo.findByBarcode(barcode)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

    ScanHistory scan = ScanHistory.builder()
      .id(UUID.randomUUID())
      .product(product)
      .user(user)
      .scannedAt(OffsetDateTime.now())
      .source("MOBILE")
      .build();
    return scanRepo.save(scan);
  }

  public List<ScanHistory> listHistory(String email) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    return scanRepo.findByUserOrderByScannedAtDesc(user);
  }

  public Favorite addFavorite(String email, UUID productId) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    Product product = productRepo.findById(productId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    Favorite fav = Favorite.builder()
      .id(new FavoriteId(user.getId(), product.getId()))
      .user(user).product(product)
      .createdAt(OffsetDateTime.now())
      .build();
    return favRepo.save(fav);
  }

  public void removeFavorite(String email, UUID productId) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    favRepo.deleteById(new FavoriteId(user.getId(), productId));
  }

  public List<Favorite> listFavorites(String email) {
    UserAccount user = userRepo.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    return favRepo.findByUser(user);
  }
}
