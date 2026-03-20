package com.gym.report.web.dto;

public record MovementSummaryResponse(
        String movementType,
        Long movementCount,
        Integer totalUnits
) {
}

