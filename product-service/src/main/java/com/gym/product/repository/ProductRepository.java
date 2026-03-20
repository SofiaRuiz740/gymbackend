package com.gym.product.repository;

import com.gym.product.domain.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {

    Mono<Boolean> existsBySku(String sku);
}

