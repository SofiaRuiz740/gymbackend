package com.gym.product.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        UUID categoryId,
        String categoryName,
        BigDecimal unitPrice,
        String brand,
        String productType,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

