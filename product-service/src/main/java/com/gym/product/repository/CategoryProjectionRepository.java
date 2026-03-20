package com.gym.product.repository;

import com.gym.product.domain.CategoryProjection;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface CategoryProjectionRepository extends ReactiveCrudRepository<CategoryProjection, UUID> {
}

