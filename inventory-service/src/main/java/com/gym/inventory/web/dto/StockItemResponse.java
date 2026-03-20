package com.gym.inventory.web.dto;

import java.time.Instant;
import java.util.UUID;

public record StockItemResponse(
        UUID productId,
        String sku,
        String productName,
        String categoryName,
        int availableStock,
        boolean active,
        Instant updatedAt
) {
}

