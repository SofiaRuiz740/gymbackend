package com.gym.shared.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductEvent(
        UUID productId,
        String sku,
        String name,
        String description,
        UUID categoryId,
        String categoryName,
        BigDecimal unitPrice,
        String brand,
        String productType,
        boolean active,
        String eventType,
        Instant occurredAt
) {
}

