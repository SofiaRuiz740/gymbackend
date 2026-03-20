package com.gym.category.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 255) String description
) {
}

