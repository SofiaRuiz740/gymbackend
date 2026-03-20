package com.gym.category.messaging;

import com.gym.category.config.KafkaTopicsProperties;
import com.gym.category.domain.Category;
import com.gym.shared.events.CategoryEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class CategoryEventPublisher {

    private final KafkaTemplate<String, CategoryEvent> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public CategoryEventPublisher(KafkaTemplate<String, CategoryEvent> kafkaTemplate,
                                  KafkaTopicsProperties kafkaTopicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    public Mono<Void> publish(Category category, String eventType) {
        CategoryEvent event = new CategoryEvent(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                eventType,
                Instant.now()
        );
        return Mono.fromFuture(kafkaTemplate.send(kafkaTopicsProperties.categoryEvents(), category.getId().toString(), event))
                .then();
    }
}
