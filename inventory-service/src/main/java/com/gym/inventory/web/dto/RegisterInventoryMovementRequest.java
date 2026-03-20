package com.gym.inventory.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterInventoryMovementRequest(
        @NotNull UUID productId,
        @Min(1) int quantity,
        @Size(max = 100) String reference,
        @Size(max = 255) String notes
) {
}

