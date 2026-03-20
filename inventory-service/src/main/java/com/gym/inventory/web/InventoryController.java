package com.gym.inventory.web;

import com.gym.inventory.service.InventoryService;
import com.gym.inventory.web.dto.InventoryMovementResponse;
import com.gym.inventory.web.dto.RegisterInventoryMovementRequest;
import com.gym.inventory.web.dto.StockItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/api/v1/movements/entries")
    public Mono<InventoryMovementResponse> registerEntry(@Valid @RequestBody RegisterInventoryMovementRequest request,
                                                         Authentication authentication) {
        return inventoryService.registerEntry(request, authentication);
    }

    @PostMapping("/api/v1/movements/exits")
    public Mono<InventoryMovementResponse> registerExit(@Valid @RequestBody RegisterInventoryMovementRequest request,
                                                        Authentication authentication) {
        return inventoryService.registerExit(request, authentication);
    }

    @GetMapping("/api/v1/stock")
    public Flux<StockItemResponse> getStock() {
        return inventoryService.getStock();
    }

    @GetMapping("/api/v1/movements")
    public Flux<InventoryMovementResponse> getMovements(@RequestParam(name = "from", required = false) Instant from,
                                                        @RequestParam(name = "to", required = false) Instant to) {
        return inventoryService.getMovements(from, to);
    }
}
