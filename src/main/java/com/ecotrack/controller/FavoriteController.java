package com.ecotrack.controller;

import com.ecotrack.dto.mobile.FavoriteCreateRequest;
import com.ecotrack.dto.mobile.FavoriteItemResponse;
import com.ecotrack.dto.mobile.FavoriteUpdateRequest;
import com.ecotrack.service.MobileFavoriteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

  private final MobileFavoriteService favoriteService;

  public FavoriteController(MobileFavoriteService favoriteService) {
    this.favoriteService = favoriteService;
  }

  @GetMapping
  public List<FavoriteItemResponse> list() {
    return favoriteService.list();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FavoriteItemResponse create(@RequestBody @Valid FavoriteCreateRequest request) {
    return favoriteService.create(request);
  }

  @PatchMapping("/{id}")
  public FavoriteItemResponse update(@PathVariable String id, @RequestBody @Valid FavoriteUpdateRequest request) {
    return favoriteService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    favoriteService.delete(id);
  }
}
