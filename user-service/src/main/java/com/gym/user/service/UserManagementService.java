package com.gym.user.service;

import com.gym.user.domain.UserAccount;
import com.gym.user.repository.UserAccountRepository;
import com.gym.user.web.dto.CreateUserRequest;
import com.gym.user.web.dto.UpdateUserStatusRequest;
import com.gym.user.web.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserManagementService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<UserResponse> createUser(CreateUserRequest request) {
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
                            .role(request.role())
                            .active(true)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    return userAccountRepository.save(userAccount).map(this::toResponse);
                });
    }

    public Flux<UserResponse> listUsers() {
        return userAccountRepository.findAll().map(this::toResponse);
    }

    public Mono<UserResponse> updateStatus(UUID userId, UpdateUserStatusRequest request) {
        return userAccountRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")))
                .flatMap(user -> {
                    user.setActive(request.active());
                    user.setUpdatedAt(Instant.now());
                    return userAccountRepository.save(user);
                })
                .map(this::toResponse);
    }

    public UserResponse toResponse(UserAccount userAccount) {
        return new UserResponse(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getEmail(),
                userAccount.getRole(),
                userAccount.isActive(),
                userAccount.getCreatedAt(),
                userAccount.getUpdatedAt()
        );
    }
}

