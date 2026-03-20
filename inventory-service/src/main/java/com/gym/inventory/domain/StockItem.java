package com.gym.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_items")
public class StockItem {

    @Id
    private UUID productId;
    private String sku;
    private String productName;
    private String categoryName;
    private int availableStock;
    private boolean active;
    private Instant updatedAt;
    @Version
    private Long version;
}
