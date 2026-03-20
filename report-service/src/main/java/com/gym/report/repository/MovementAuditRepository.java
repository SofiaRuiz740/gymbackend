package com.gym.report.repository;

import com.gym.report.domain.MovementAudit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

public interface MovementAuditRepository extends ReactiveCrudRepository<MovementAudit, UUID> {

    @Query("SELECT * FROM reporting.movement_audit WHERE occurred_at BETWEEN :from AND :to ORDER BY occurred_at DESC")
    Flux<MovementAudit> findBetween(@Param("from") Instant from, @Param("to") Instant to);
}
