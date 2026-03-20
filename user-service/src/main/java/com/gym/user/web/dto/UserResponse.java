package com.gym.user.web.dto;

import com.gym.user.domain.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

