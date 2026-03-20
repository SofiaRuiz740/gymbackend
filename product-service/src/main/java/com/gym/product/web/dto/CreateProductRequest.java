package com.gym.product.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank @Size(max = 50) String sku,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 255) String description,
        @NotNull UUID categoryId,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
        @NotBlank @Size(max = 80) String brand,
        @NotBlank @Size(max = 80) String productType
) {
}

