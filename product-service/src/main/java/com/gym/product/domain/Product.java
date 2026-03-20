package com.gym.product.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    @Id
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private UUID categoryId;
    private String categoryName;
    private BigDecimal unitPrice;
    private String brand;
    private String productType;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    @Version
    private Long version;
}
