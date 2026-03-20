package com.gym.inventory.repository;

import com.gym.inventory.domain.InventoryMovement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

public interface InventoryMovementRepository extends ReactiveCrudRepository<InventoryMovement, UUID> {

    Flux<InventoryMovement> findAllByOrderByOccurredAtDesc();

    Flux<InventoryMovement> findAllByOccurredAtBetweenOrderByOccurredAtDesc(Instant from, Instant to);
}

