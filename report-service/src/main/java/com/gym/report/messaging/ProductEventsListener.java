package com.gym.report.messaging;

import com.gym.report.service.ProjectionUpdaterService;
import com.gym.shared.events.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductEventsListener {

    private final ProjectionUpdaterService projectionUpdaterService;

    public ProductEventsListener(ProjectionUpdaterService projectionUpdaterService) {
        this.projectionUpdaterService = projectionUpdaterService;
    }

    @KafkaListener(topics = "${app.kafka.topics.product-events}", groupId = "${spring.application.name}")
    public void onProductEvent(ProductEvent event) {
        projectionUpdaterService.applyProductEvent(event)
                .doOnError(error -> log.error("Error procesando ProductEvent {}", event.productId(), error))
                .subscribe();
    }
}

