package com.gym.shared.events;

import java.time.Instant;
import java.util.UUID;

public record CategoryEvent(
        UUID categoryId,
        String code,
        String name,
        String description,
        boolean active,
        String eventType,
        Instant occurredAt
) {
}

