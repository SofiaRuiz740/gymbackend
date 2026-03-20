package com.gym.product.messaging;

import com.gym.product.domain.CategoryProjection;
import com.gym.product.repository.CategoryProjectionRepository;
import com.gym.shared.events.CategoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CategoryEventsListener {

    private final CategoryProjectionRepository categoryProjectionRepository;

    public CategoryEventsListener(CategoryProjectionRepository categoryProjectionRepository) {
        this.categoryProjectionRepository = categoryProjectionRepository;
    }

    @KafkaListener(topics = "${app.kafka.topics.category-events}", groupId = "${spring.application.name}")
    public void onCategoryEvent(CategoryEvent event) {
        CategoryProjection projection = CategoryProjection.builder()
                .id(event.categoryId())
                .code(event.code())
                .name(event.name())
                .description(event.description())
                .active(event.active())
                .updatedAt(event.occurredAt())
                .build();

        categoryProjectionRepository.save(projection)
                .doOnError(error -> log.error("Error procesando CategoryEvent {}", event.categoryId(), error))
                .subscribe();
    }
}

