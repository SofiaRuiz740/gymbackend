package com.gym.user.web.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        String username,
        String role
) {
}

