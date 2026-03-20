package com.gym.report.service;

import com.gym.report.repository.MovementAuditRepository;
import com.gym.report.repository.ReportProductViewRepository;
import com.gym.report.web.dto.MovementAuditResponse;
import com.gym.report.web.dto.MovementSummaryResponse;
import com.gym.report.web.dto.ReportProductResponse;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Service
public class ReportingService {

    private final ReportProductViewRepository reportProductViewRepository;
    private final MovementAuditRepository movementAuditRepository;
    private final DatabaseClient databaseClient;

    public ReportingService(ReportProductViewRepository reportProductViewRepository,
                            MovementAuditRepository movementAuditRepository,
                            DatabaseClient databaseClient) {
        this.reportProductViewRepository = reportProductViewRepository;
        this.movementAuditRepository = movementAuditRepository;
        this.databaseClient = databaseClient;
    }

    public Flux<ReportProductResponse> getStockReport() {
        return reportProductViewRepository.findAllOrdered()
                .map(view -> new ReportProductResponse(
                        view.getProductId(),
                        view.getSku(),
                        view.getProductName(),
                        view.getCategoryId(),
                        view.getCategoryName(),
                        view.getCurrentStock(),
                        view.getTotalEntries(),
                        view.getTotalExits(),
                        view.isActive(),
                        view.getUpdatedAt()
                ));
    }

    public Flux<ReportProductResponse> getLowStockReport(int threshold) {
        return reportProductViewRepository.findLowStock(threshold)
                .map(view -> new ReportProductResponse(
                        view.getProductId(),
                        view.getSku(),
                        view.getProductName(),
                        view.getCategoryId(),
                        view.getCategoryName(),
                        view.getCurrentStock(),
                        view.getTotalEntries(),
                        view.getTotalExits(),
                        view.isActive(),
                        view.getUpdatedAt()
                ));
    }

    public Flux<MovementAuditResponse> getMovementAudit(Instant from, Instant to) {
        return movementAuditRepository.findBetween(from, to)
                .map(movement -> new MovementAuditResponse(
                        movement.getMovementId(),
                        movement.getProductId(),
                        movement.getSku(),
                        movement.getProductName(),
                        movement.getCategoryName(),
                        movement.getMovementType(),
                        movement.getQuantity(),
                        movement.getResultingStock(),
                        movement.getReference(),
                        movement.getNotes(),
                        movement.getRegisteredBy(),
                        movement.getOccurredAt()
                ));
    }

    public Flux<MovementSummaryResponse> getMovementSummary(Instant from, Instant to) {
        return databaseClient.sql("""
                        SELECT movement_type, COUNT(*) AS movement_count, COALESCE(SUM(quantity), 0) AS total_units
                        FROM reporting.movement_audit
                        WHERE occurred_at BETWEEN :from AND :to
                        GROUP BY movement_type
                        ORDER BY movement_type
                        """)
                .bind("from", from)
                .bind("to", to)
                .map((row, metadata) -> new MovementSummaryResponse(
                        row.get("movement_type", String.class),
                        row.get("movement_count", Long.class),
                        row.get("total_units", Integer.class)
                ))
                .all();
    }
}

