package com.ecotrack.controller;

import com.ecotrack.domain.Favorite;
import com.ecotrack.domain.ScanHistory;
import com.ecotrack.service.ScanService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {
  private final ScanService service;

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public ScanHistory record(@RequestBody ScanRequest req) {
    return service.recordScan(req.getEmail(), req.getBarcode());
  }

  @GetMapping("/history")
  public List<ScanHistory> history(@RequestParam String email) {
    return service.listHistory(email);
  }

  @PostMapping("/favorite/{productId}") @ResponseStatus(HttpStatus.CREATED)
  public Favorite addFav(@RequestParam String email, @PathVariable UUID productId) {
    return service.addFavorite(email, productId);
  }

  @DeleteMapping("/favorite/{productId}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delFav(@RequestParam String email, @PathVariable UUID productId) {
    service.removeFavorite(email, productId);
  }

  @GetMapping("/favorites")
  public List<Favorite> listFav(@RequestParam String email) {
    return service.listFavorites(email);
  }

  @Data
  public static class ScanRequest {
    @NotBlank private String email;
    @NotBlank private String barcode;
  }
}
