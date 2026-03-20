package com.gym.report.domain;

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
@Table("report_product_view")
public class ReportProductView {

    @Id
    private UUID productId;
    private String sku;
    private String productName;
    private UUID categoryId;
    private String categoryName;
    private int currentStock;
    private int totalEntries;
    private int totalExits;
    private boolean active;
    private Instant updatedAt;
    @Version
    private Long version;
}
