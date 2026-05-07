package com.gym.user.service;

import com.gym.user.domain.Role;
import com.gym.user.domain.UserAccount;
import com.gym.user.repository.UserAccountRepository;
import com.gym.user.security.JwtTokenService;
import com.gym.user.web.dto.LoginRequest;
import com.gym.user.web.dto.LoginResponse;
import com.gym.user.web.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

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
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas")))
                .map(this::toLoginResponse);
    }

    public Mono<LoginResponse> register(RegisterRequest request) {
        return Mono.zip(
                        userAccountRepository.existsByUsername(request.username()),
                        userAccountRepository.existsByEmail(request.email())
                )
                .flatMap(existing -> {
                    if (existing.getT1()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe"));
                    }
                    if (existing.getT2()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El email ya existe"));
                    }

                    Instant now = Instant.now();
                    UserAccount userAccount = UserAccount.builder()
                            .id(UUID.randomUUID())
                            .username(request.username())
                            .email(request.email())
                            .passwordHash(passwordEncoder.encode(request.password()))
                            .role(Role.ROLE_USER)
                            .active(true)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    return userAccountRepository.save(userAccount);
                })
                .map(this::toLoginResponse);
    }

    private LoginResponse toLoginResponse(UserAccount user) {
        return new LoginResponse(
                jwtTokenService.generateToken(user),
                "Bearer",
                jwtTokenService.calculateExpiration(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
