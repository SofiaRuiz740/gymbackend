package com.gym.inventory.messaging;

import com.gym.inventory.domain.ProductSnapshot;
import com.gym.inventory.domain.StockItem;
import com.gym.inventory.repository.ProductSnapshotRepository;
import com.gym.inventory.repository.StockItemRepository;
import com.gym.shared.events.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductEventsListener {

    private final ProductSnapshotRepository productSnapshotRepository;
    private final StockItemRepository stockItemRepository;

    public ProductEventsListener(ProductSnapshotRepository productSnapshotRepository,
                                 StockItemRepository stockItemRepository) {
        this.productSnapshotRepository = productSnapshotRepository;
        this.stockItemRepository = stockItemRepository;
    }

    @KafkaListener(topics = "${app.kafka.topics.product-events}", groupId = "${spring.application.name}")
    public void onProductEvent(ProductEvent event) {
        ProductSnapshot productSnapshot = ProductSnapshot.builder()
                .id(event.productId())
                .sku(event.sku())
                .productName(event.name())
                .categoryName(event.categoryName())
                .active(event.active())
                .updatedAt(event.occurredAt())
                .build();

        productSnapshotRepository.save(productSnapshot)
                .then(stockItemRepository.findById(event.productId())
                        .defaultIfEmpty(StockItem.builder()
                                .productId(event.productId())
                                .availableStock(0)
                                .build())
                        .flatMap(stockItem -> {
                            stockItem.setProductId(event.productId());
                            stockItem.setSku(event.sku());
                            stockItem.setProductName(event.name());
                            stockItem.setCategoryName(event.categoryName());
                            stockItem.setActive(event.active());
                            stockItem.setUpdatedAt(event.occurredAt());
                            return stockItemRepository.save(stockItem);
                        }))
                .doOnError(error -> log.error("Error procesando ProductEvent {}", event.productId(), error))
                .subscribe();
    }
}

