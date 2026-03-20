package com.gym.inventory.repository;

import com.gym.inventory.domain.ProductSnapshot;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProductSnapshotRepository extends ReactiveCrudRepository<ProductSnapshot, UUID> {
}

