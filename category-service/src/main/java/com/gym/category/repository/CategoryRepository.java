package com.gym.category.repository;

import com.gym.category.domain.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CategoryRepository extends ReactiveCrudRepository<Category, UUID> {

    Mono<Boolean> existsByCode(String code);
}

