package com.gym.user.service;

import com.gym.user.repository.UserAccountRepository;
import com.gym.user.security.JwtTokenService;
import com.gym.user.web.dto.LoginRequest;
import com.gym.user.web.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public Mono<LoginResponse> login(LoginRequest request) {
        return userAccountRepository.findByUsername(request.username())
                .filter(user -> user.isActive() && passwordEncoder.matches(request.password(), user.getPasswordHash()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
                .map(user -> new LoginResponse(
                        jwtTokenService.generateToken(user),
                        "Bearer",
                        jwtTokenService.calculateExpiration(),
                        user.getUsername(),
                        user.getRole().name()
                ));
    }
}

