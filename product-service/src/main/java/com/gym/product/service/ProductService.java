package com.gym.product.service;

import com.gym.product.domain.CategoryProjection;
import com.gym.product.domain.Product;
import com.gym.product.messaging.ProductEventPublisher;
import com.gym.product.repository.CategoryProjectionRepository;
import com.gym.product.repository.ProductRepository;
import com.gym.product.web.dto.CreateProductRequest;
import com.gym.product.web.dto.ProductResponse;
import com.gym.product.web.dto.UpdateProductRequest;
import com.gym.product.web.dto.UpdateProductStatusRequest;
import com.gym.shared.events.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryProjectionRepository categoryProjectionRepository;
    private final ProductEventPublisher eventPublisher;

    public ProductService(ProductRepository productRepository,
                          CategoryProjectionRepository categoryProjectionRepository,
                          ProductEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.categoryProjectionRepository = categoryProjectionRepository;
        this.eventPublisher = eventPublisher;
    }

    public Mono<ProductResponse> create(CreateProductRequest request) {
        return productRepository.existsBySku(request.sku())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya existe"));
                    }
                    return requireActiveCategory(request.categoryId())
                            .flatMap(category -> {
                                Instant now = Instant.now();
                                Product product = Product.builder()
                                        .id(UUID.randomUUID())
                                        .sku(request.sku())
                                        .name(request.name())
                                        .description(request.description())
                                        .categoryId(category.getId())
                                        .categoryName(category.getName())
                                        .unitPrice(request.unitPrice())
                                        .brand(request.brand())
                                        .productType(request.productType())
                                        .active(true)
                                        .createdAt(now)
                                        .updatedAt(now)
                                        .build();
                                return productRepository.save(product)
                                        .delayUntil(saved -> eventPublisher.publish(saved, EventType.UPSERT))
                                        .map(this::toResponse);
                            });
                });
    }

    public Mono<ProductResponse> update(UUID productId, UpdateProductRequest request) {
        return Mono.zip(
                        productRepository.findById(productId)
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"))),
                        requireActiveCategory(request.categoryId())
                )
                .flatMap(tuple -> {
                    Product product = tuple.getT1();
                    CategoryProjection category = tuple.getT2();
                    product.setName(request.name());
                    product.setDescription(request.description());
                    product.setCategoryId(category.getId());
                    product.setCategoryName(category.getName());
                    product.setUnitPrice(request.unitPrice());
                    product.setBrand(request.brand());
                    product.setProductType(request.productType());
                    product.setUpdatedAt(Instant.now());
                    return productRepository.save(product);
                })
                .delayUntil(product -> eventPublisher.publish(product, EventType.UPSERT))
                .map(this::toResponse);
    }

    public Mono<ProductResponse> updateStatus(UUID productId, UpdateProductStatusRequest request) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")))
                .flatMap(product -> {
                    product.setActive(request.active());
                    product.setUpdatedAt(Instant.now());
                    return productRepository.save(product);
                })
                .delayUntil(product -> eventPublisher.publish(product, EventType.STATUS_CHANGED))
                .map(this::toResponse);
    }

    public Flux<ProductResponse> findAll() {
        return productRepository.findAll().map(this::toResponse);
    }

    public Mono<ProductResponse> findById(UUID productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")))
                .map(this::toResponse);
    }

    private Mono<CategoryProjection> requireActiveCategory(UUID categoryId) {
        return categoryProjectionRepository.findById(categoryId)
                .filter(CategoryProjection::isActive)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe o no está activa")));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getCategoryName(),
                product.getUnitPrice(),
                product.getBrand(),
                product.getProductType(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}

