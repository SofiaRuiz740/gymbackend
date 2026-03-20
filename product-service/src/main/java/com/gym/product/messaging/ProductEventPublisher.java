package com.gym.product.messaging;

import com.gym.product.config.KafkaTopicsProperties;
import com.gym.product.domain.Product;
import com.gym.shared.events.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class ProductEventPublisher {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public ProductEventPublisher(KafkaTemplate<String, ProductEvent> kafkaTemplate,
                                 KafkaTopicsProperties kafkaTopicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    public Mono<Void> publish(Product product, String eventType) {
        ProductEvent event = new ProductEvent(
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
                eventType,
                Instant.now()
        );
        return Mono.fromFuture(kafkaTemplate.send(kafkaTopicsProperties.productEvents(), product.getId().toString(), event))
                .then();
    }
}
