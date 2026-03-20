package com.gym.user.repository;

import com.gym.user.domain.UserAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAccountRepository extends ReactiveCrudRepository<UserAccount, UUID> {

    Mono<UserAccount> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);
}

