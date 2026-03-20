package com.gym.report.service;

import com.gym.shared.events.CategoryEvent;
import com.gym.shared.events.InventoryMovementEvent;
import com.gym.shared.events.ProductEvent;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProjectionUpdaterService {

    private final DatabaseClient databaseClient;

    public ProjectionUpdaterService(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Void> applyCategoryEvent(CategoryEvent event) {
        return databaseClient.sql("""
                        UPDATE reporting.report_product_view
                        SET category_name = :categoryName,
                            updated_at = :updatedAt
                        WHERE category_id = :categoryId
                        """)
                .bind("categoryName", event.name())
                .bind("updatedAt", event.occurredAt())
                .bind("categoryId", event.categoryId())
                .fetch()
                .rowsUpdated()
                .then();
    }

    public Mono<Void> applyProductEvent(ProductEvent event) {
        return databaseClient.sql("""
                        INSERT INTO reporting.report_product_view (
                            product_id, sku, product_name, category_id, category_name,
                            current_stock, total_entries, total_exits, active, updated_at
                        ) VALUES (
                            :productId, :sku, :productName, :categoryId, :categoryName,
                            0, 0, 0, :active, :updatedAt
                        )
                        ON CONFLICT (product_id) DO UPDATE SET
                            sku = :sku,
                            product_name = :productName,
                            category_id = :categoryId,
                            category_name = :categoryName,
                            active = :active,
                            updated_at = :updatedAt
                        """)
                .bind("productId", event.productId())
                .bind("sku", event.sku())
                .bind("productName", event.name())
                .bind("categoryId", event.categoryId())
                .bind("categoryName", event.categoryName())
                .bind("active", event.active())
                .bind("updatedAt", event.occurredAt())
                .fetch()
                .rowsUpdated()
                .then();
    }

    public Mono<Void> applyInventoryMovementEvent(InventoryMovementEvent event) {
        int entryDelta = "ENTRY".equalsIgnoreCase(event.movementType()) ? event.quantity() : 0;
        int exitDelta = "EXIT".equalsIgnoreCase(event.movementType()) ? event.quantity() : 0;

        Mono<Void> upsertProjection = databaseClient.sql("""
                        INSERT INTO reporting.report_product_view (
                            product_id, sku, product_name, category_id, category_name,
                            current_stock, total_entries, total_exits, active, updated_at
                        ) VALUES (
                            :productId, :sku, :productName, NULL, :categoryName,
                            :currentStock, :totalEntries, :totalExits, TRUE, :updatedAt
                        )
                        ON CONFLICT (product_id) DO UPDATE SET
                            sku = :sku,
                            product_name = :productName,
                            category_name = :categoryName,
                            current_stock = :currentStock,
                            total_entries = reporting.report_product_view.total_entries + :totalEntries,
                            total_exits = reporting.report_product_view.total_exits + :totalExits,
                            updated_at = :updatedAt
                        """)
                .bind("productId", event.productId())
                .bind("sku", event.sku())
                .bind("productName", event.productName())
                .bind("categoryName", event.categoryName())
                .bind("currentStock", event.resultingStock())
                .bind("totalEntries", entryDelta)
                .bind("totalExits", exitDelta)
                .bind("updatedAt", event.occurredAt())
                .fetch()
                .rowsUpdated()
                .then();

        Mono<Void> insertMovement = databaseClient.sql("""
                        INSERT INTO reporting.movement_audit (
                            movement_id, product_id, sku, product_name, category_name,
                            movement_type, quantity, resulting_stock, reference, notes, registered_by, occurred_at
                        ) VALUES (
                            :movementId, :productId, :sku, :productName, :categoryName,
                            :movementType, :quantity, :resultingStock, :reference, :notes, :registeredBy, :occurredAt
                        )
                        ON CONFLICT (movement_id) DO NOTHING
                        """)
                .bind("movementId", event.movementId())
                .bind("productId", event.productId())
                .bind("sku", event.sku())
                .bind("productName", event.productName())
                .bind("categoryName", event.categoryName())
                .bind("movementType", event.movementType())
                .bind("quantity", event.quantity())
                .bind("resultingStock", event.resultingStock())
                .bind("reference", safe(event.reference()))
                .bind("notes", safe(event.notes()))
                .bind("registeredBy", event.registeredBy())
                .bind("occurredAt", event.occurredAt())
                .fetch()
                .rowsUpdated()
                .then();

        return upsertProjection.then(insertMovement);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}

