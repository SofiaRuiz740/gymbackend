package com.gym.user.web;

import com.gym.user.service.AuthService;
import com.gym.user.web.dto.LoginRequest;
import com.gym.user.web.dto.LoginResponse;
import com.gym.user.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public Mono<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
