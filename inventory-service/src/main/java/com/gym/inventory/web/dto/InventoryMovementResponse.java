package com.gym.inventory.web.dto;

import com.gym.inventory.domain.MovementType;

import java.time.Instant;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        UUID productId,
        String sku,
        String productName,
        String categoryName,
        MovementType movementType,
        int quantity,
        int resultingStock,
        String reference,
        String notes,
        String registeredBy,
        Instant occurredAt
) {
}

