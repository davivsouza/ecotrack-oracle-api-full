package com.ecotrack.controller;

import com.ecotrack.dto.auth.AuthLoginRequest;
import com.ecotrack.dto.auth.AuthRegisterRequest;
import com.ecotrack.dto.auth.AuthResponse;
import com.ecotrack.dto.auth.AuthUserResponse;
import com.ecotrack.security.CurrentUserService;
import com.ecotrack.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final CurrentUserService currentUserService;

  public AuthController(AuthService authService, CurrentUserService currentUserService) {
    this.authService = authService;
    this.currentUserService = currentUserService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@RequestBody @Valid AuthRegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid AuthLoginRequest request) {
    return authService.login(request);
  }

  @GetMapping("/me")
  public AuthUserResponse me() {
    return authService.me(currentUserService.requireAuthenticatedPrincipal());
  }
}
