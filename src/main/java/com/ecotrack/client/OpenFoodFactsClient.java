package com.ecotrack.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "openFoodFactsClient", url = "${ecotrack.open-food-facts-url:https://world.openfoodfacts.org/api/v2}")
public interface OpenFoodFactsClient {

  @GetMapping("/product/{barcode}.json")
  Map<String, Object> findByBarcode(@PathVariable("barcode") String barcode);
}
