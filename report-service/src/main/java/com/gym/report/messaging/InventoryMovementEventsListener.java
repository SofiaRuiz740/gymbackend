package com.gym.report.messaging;

import com.gym.report.service.ProjectionUpdaterService;
import com.gym.shared.events.InventoryMovementEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryMovementEventsListener {

    private final ProjectionUpdaterService projectionUpdaterService;

    public InventoryMovementEventsListener(ProjectionUpdaterService projectionUpdaterService) {
        this.projectionUpdaterService = projectionUpdaterService;
    }

    @KafkaListener(topics = "${app.kafka.topics.inventory-movements}", groupId = "${spring.application.name}")
    public void onInventoryMovementEvent(InventoryMovementEvent event) {
        projectionUpdaterService.applyInventoryMovementEvent(event)
                .doOnError(error -> log.error("Error procesando InventoryMovementEvent {}", event.movementId(), error))
                .subscribe();
    }
}

