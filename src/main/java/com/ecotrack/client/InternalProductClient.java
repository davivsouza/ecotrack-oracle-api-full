package com.ecotrack.client;

import com.ecotrack.dto.mobile.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "internalProductClient", url = "${ecotrack.internal-api.base-url:http://localhost:8080}")
public interface InternalProductClient {

  @GetMapping("/products/{id}")
  ProductResponse getById(@PathVariable("id") String id);
}
