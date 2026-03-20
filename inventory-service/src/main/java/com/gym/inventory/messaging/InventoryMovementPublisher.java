package com.gym.inventory.messaging;

import com.gym.inventory.config.KafkaTopicsProperties;
import com.gym.inventory.domain.InventoryMovement;
import com.gym.shared.events.InventoryMovementEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InventoryMovementPublisher {

    private final KafkaTemplate<String, InventoryMovementEvent> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public InventoryMovementPublisher(KafkaTemplate<String, InventoryMovementEvent> kafkaTemplate,
                                      KafkaTopicsProperties kafkaTopicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    public Mono<Void> publish(InventoryMovement movement) {
        InventoryMovementEvent event = new InventoryMovementEvent(
                movement.getId(),
                movement.getProductId(),
                movement.getSku(),
                movement.getProductName(),
                movement.getCategoryName(),
                movement.getMovementType().name(),
                movement.getQuantity(),
                movement.getResultingStock(),
                movement.getReference(),
                movement.getNotes(),
                movement.getRegisteredBy(),
                movement.getOccurredAt()
        );
        return Mono.fromFuture(kafkaTemplate.send(kafkaTopicsProperties.inventoryMovements(), movement.getId().toString(), event))
                .then();
    }
}
