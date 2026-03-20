package com.gym.category.service;

import com.gym.category.domain.Category;
import com.gym.category.messaging.CategoryEventPublisher;
import com.gym.category.repository.CategoryRepository;
import com.gym.category.web.dto.CategoryResponse;
import com.gym.category.web.dto.CreateCategoryRequest;
import com.gym.category.web.dto.UpdateCategoryRequest;
import com.gym.category.web.dto.UpdateCategoryStatusRequest;
import com.gym.shared.events.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryEventPublisher eventPublisher;

    public CategoryService(CategoryRepository categoryRepository, CategoryEventPublisher eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    public Mono<CategoryResponse> create(CreateCategoryRequest request) {
        return categoryRepository.existsByCode(request.code())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El código de categoría ya existe"));
                    }

                    Instant now = Instant.now();
                    Category category = Category.builder()
                            .id(UUID.randomUUID())
                            .code(request.code())
                            .name(request.name())
                            .description(request.description())
                            .active(true)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    return categoryRepository.save(category)
                            .delayUntil(saved -> eventPublisher.publish(saved, EventType.UPSERT))
                            .map(this::toResponse);
                });
    }

    public Mono<CategoryResponse> update(UUID categoryId, UpdateCategoryRequest request) {
        return categoryRepository.findById(categoryId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada")))
                .flatMap(category -> {
                    category.setName(request.name());
                    category.setDescription(request.description());
                    category.setUpdatedAt(Instant.now());
                    return categoryRepository.save(category);
                })
                .delayUntil(category -> eventPublisher.publish(category, EventType.UPSERT))
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> updateStatus(UUID categoryId, UpdateCategoryStatusRequest request) {
        return categoryRepository.findById(categoryId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada")))
                .flatMap(category -> {
                    category.setActive(request.active());
                    category.setUpdatedAt(Instant.now());
                    return categoryRepository.save(category);
                })
                .delayUntil(category -> eventPublisher.publish(category, EventType.STATUS_CHANGED))
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> findById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada")))
                .map(this::toResponse);
    }

    public Flux<CategoryResponse> findAll() {
        return categoryRepository.findAll().map(this::toResponse);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}

