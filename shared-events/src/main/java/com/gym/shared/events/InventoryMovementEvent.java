package com.gym.shared.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryMovementEvent(
        UUID movementId,
        UUID productId,
        String sku,
        String productName,
        String categoryName,
        String movementType,
        int quantity,
        int resultingStock,
        String reference,
        String notes,
        String registeredBy,
        Instant occurredAt
) {
}

