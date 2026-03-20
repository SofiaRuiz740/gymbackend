package com.gym.report.web;

import com.gym.report.service.ReportingService;
import com.gym.report.web.dto.MovementAuditResponse;
import com.gym.report.web.dto.MovementSummaryResponse;
import com.gym.report.web.dto.ReportProductResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Instant;

@RestController
@RequestMapping(path = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/stock")
    public Flux<ReportProductResponse> getStockReport() {
        return reportingService.getStockReport();
    }

    @GetMapping("/low-stock")
    public Flux<ReportProductResponse> getLowStockReport(@RequestParam(name = "threshold", defaultValue = "10") int threshold) {
        return reportingService.getLowStockReport(threshold);
    }

    @GetMapping("/movements")
    public Flux<MovementAuditResponse> getMovementAudit(@RequestParam("from") Instant from,
                                                        @RequestParam("to") Instant to) {
        return reportingService.getMovementAudit(from, to);
    }

    @GetMapping("/movements/summary")
    public Flux<MovementSummaryResponse> getMovementSummary(@RequestParam("from") Instant from,
                                                            @RequestParam("to") Instant to) {
        return reportingService.getMovementSummary(from, to);
    }
}
