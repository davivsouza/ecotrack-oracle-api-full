package com.ecotrack;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class EcoTrackOracleApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(EcoTrackOracleApiApplication.class, args);
  }
}
