package com.ecotrack.controller;

import com.ecotrack.dto.mobile.HistoryCreateRequest;
import com.ecotrack.dto.mobile.HistoryItemResponse;
import com.ecotrack.dto.mobile.HistoryUpdateRequest;
import com.ecotrack.service.MobileHistoryService;
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
@RequestMapping("/history")
public class HistoryController {

  private final MobileHistoryService historyService;

  public HistoryController(MobileHistoryService historyService) {
    this.historyService = historyService;
  }

  @GetMapping
  public List<HistoryItemResponse> list() {
    return historyService.list();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HistoryItemResponse create(@RequestBody @Valid HistoryCreateRequest request) {
    return historyService.create(request);
  }

  @PatchMapping("/{id}")
  public HistoryItemResponse update(@PathVariable String id, @RequestBody @Valid HistoryUpdateRequest request) {
    return historyService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    historyService.delete(id);
  }
}
