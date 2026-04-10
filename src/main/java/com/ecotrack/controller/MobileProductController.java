package com.ecotrack.controller;

import com.ecotrack.dto.mobile.ProductResponse;
import com.ecotrack.service.MobileProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class MobileProductController {

  private final MobileProductService mobileProductService;

  public MobileProductController(MobileProductService mobileProductService) {
    this.mobileProductService = mobileProductService;
  }

  @GetMapping
  public List<ProductResponse> list(@RequestParam(required = false) String search) {
    return mobileProductService.list(search);
  }

  @GetMapping("/barcode/{barcode}")
  public ProductResponse byBarcode(@PathVariable String barcode) {
    return mobileProductService.getByBarcode(barcode);
  }

  @GetMapping("/{id}")
  public ProductResponse byId(@PathVariable String id) {
    return mobileProductService.getById(id);
  }
}
