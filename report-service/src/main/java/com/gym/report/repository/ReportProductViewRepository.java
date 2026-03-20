package com.gym.report.repository;

import com.gym.report.domain.ReportProductView;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ReportProductViewRepository extends ReactiveCrudRepository<ReportProductView, UUID> {

    @Query("SELECT * FROM reporting.report_product_view ORDER BY product_name ASC")
    Flux<ReportProductView> findAllOrdered();

    @Query("SELECT * FROM reporting.report_product_view WHERE current_stock <= :threshold ORDER BY current_stock ASC, product_name ASC")
    Flux<ReportProductView> findLowStock(@Param("threshold") int threshold);
}
