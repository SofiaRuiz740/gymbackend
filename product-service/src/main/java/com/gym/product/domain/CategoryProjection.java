package com.gym.product.domain;

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
@Table("category_projection")
public class CategoryProjection {

    @Id
    private UUID id;
    private String code;
    private String name;
    private String description;
    private boolean active;
    private Instant updatedAt;
    @Version
    private Long version;
}
