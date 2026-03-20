package com.gym.report.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ReportProductResponse(
        UUID productId,
        String sku,
        String productName,
        UUID categoryId,
        String categoryName,
        int currentStock,
        int totalEntries,
        int totalExits,
        boolean active,
        Instant updatedAt
) {
}

