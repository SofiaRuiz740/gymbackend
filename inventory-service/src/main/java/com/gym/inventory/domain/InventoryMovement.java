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
@Table("inventory_movements")
public class InventoryMovement {

    @Id
    private UUID id;
    private UUID productId;
    private String sku;
    private String productName;
    private String categoryName;
    private MovementType movementType;
    private int quantity;
    private int resultingStock;
    private String reference;
    private String notes;
    private String registeredBy;
    private Instant occurredAt;
    @Version
    private Long version;
}
