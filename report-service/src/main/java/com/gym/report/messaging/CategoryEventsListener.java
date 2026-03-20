package com.gym.report.messaging;

import com.gym.report.service.ProjectionUpdaterService;
import com.gym.shared.events.CategoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CategoryEventsListener {

    private final ProjectionUpdaterService projectionUpdaterService;

    public CategoryEventsListener(ProjectionUpdaterService projectionUpdaterService) {
        this.projectionUpdaterService = projectionUpdaterService;
    }

    @KafkaListener(topics = "${app.kafka.topics.category-events}", groupId = "${spring.application.name}")
    public void onCategoryEvent(CategoryEvent event) {
        projectionUpdaterService.applyCategoryEvent(event)
                .doOnError(error -> log.error("Error procesando CategoryEvent {}", event.categoryId(), error))
                .subscribe();
    }
}

