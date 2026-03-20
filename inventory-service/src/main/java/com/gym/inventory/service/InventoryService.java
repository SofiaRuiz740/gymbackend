package com.gym.inventory.service;

import com.gym.inventory.domain.InventoryMovement;
import com.gym.inventory.domain.MovementType;
import com.gym.inventory.domain.ProductSnapshot;
import com.gym.inventory.domain.StockItem;
import com.gym.inventory.messaging.InventoryMovementPublisher;
import com.gym.inventory.repository.InventoryMovementRepository;
import com.gym.inventory.repository.ProductSnapshotRepository;
import com.gym.inventory.repository.StockItemRepository;
import com.gym.inventory.web.dto.InventoryMovementResponse;
import com.gym.inventory.web.dto.RegisterInventoryMovementRequest;
import com.gym.inventory.web.dto.StockItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class InventoryService {

    private final ProductSnapshotRepository productSnapshotRepository;
    private final StockItemRepository stockItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryMovementPublisher inventoryMovementPublisher;

    public InventoryService(ProductSnapshotRepository productSnapshotRepository,
                            StockItemRepository stockItemRepository,
                            InventoryMovementRepository inventoryMovementRepository,
                            InventoryMovementPublisher inventoryMovementPublisher) {
        this.productSnapshotRepository = productSnapshotRepository;
        this.stockItemRepository = stockItemRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryMovementPublisher = inventoryMovementPublisher;
    }

    @Transactional
    public Mono<InventoryMovementResponse> registerEntry(RegisterInventoryMovementRequest request, Authentication authentication) {
        return registerMovement(request, MovementType.ENTRY, resolveUsername(authentication));
    }

    @Transactional
    public Mono<InventoryMovementResponse> registerExit(RegisterInventoryMovementRequest request, Authentication authentication) {
        return registerMovement(request, MovementType.EXIT, resolveUsername(authentication));
    }

    public Flux<StockItemResponse> getStock() {
        return stockItemRepository.findAll()
                .map(stockItem -> new StockItemResponse(
                        stockItem.getProductId(),
                        stockItem.getSku(),
                        stockItem.getProductName(),
                        stockItem.getCategoryName(),
                        stockItem.getAvailableStock(),
                        stockItem.isActive(),
                        stockItem.getUpdatedAt()
                ));
    }

    public Flux<InventoryMovementResponse> getMovements(Instant from, Instant to) {
        Flux<InventoryMovement> source = from != null && to != null
                ? inventoryMovementRepository.findAllByOccurredAtBetweenOrderByOccurredAtDesc(from, to)
                : inventoryMovementRepository.findAllByOrderByOccurredAtDesc();

        return source.map(this::toResponse);
    }

    private Mono<InventoryMovementResponse> registerMovement(RegisterInventoryMovementRequest request,
                                                             MovementType movementType,
                                                             String username) {
        return requireActiveProduct(request.productId())
                .flatMap(productSnapshot -> stockItemRepository.findById(request.productId())
                        .switchIfEmpty(Mono.just(StockItem.builder()
                                .productId(productSnapshot.getId())
                                .sku(productSnapshot.getSku())
                                .productName(productSnapshot.getProductName())
                                .categoryName(productSnapshot.getCategoryName())
                                .availableStock(0)
                                .active(productSnapshot.isActive())
                                .updatedAt(Instant.now())
                                .build()))
                        .flatMap(stockItem -> {
                            int resultingStock = movementType == MovementType.ENTRY
                                    ? stockItem.getAvailableStock() + request.quantity()
                                    : stockItem.getAvailableStock() - request.quantity();

                            if (resultingStock < 0) {
                                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para registrar la salida"));
                            }

                            Instant occurredAt = Instant.now();
                            stockItem.setAvailableStock(resultingStock);
                            stockItem.setSku(productSnapshot.getSku());
                            stockItem.setProductName(productSnapshot.getProductName());
                            stockItem.setCategoryName(productSnapshot.getCategoryName());
                            stockItem.setActive(productSnapshot.isActive());
                            stockItem.setUpdatedAt(occurredAt);

                            InventoryMovement movement = InventoryMovement.builder()
                                    .id(UUID.randomUUID())
                                    .productId(productSnapshot.getId())
                                    .sku(productSnapshot.getSku())
                                    .productName(productSnapshot.getProductName())
                                    .categoryName(productSnapshot.getCategoryName())
                                    .movementType(movementType)
                                    .quantity(request.quantity())
                                    .resultingStock(resultingStock)
                                    .reference(request.reference())
                                    .notes(request.notes())
                                    .registeredBy(username)
                                    .occurredAt(occurredAt)
                                    .build();

                            return stockItemRepository.save(stockItem)
                                    .then(inventoryMovementRepository.save(movement))
                                    .delayUntil(inventoryMovementPublisher::publish)
                                    .map(this::toResponse);
                        }));
    }

    private Mono<ProductSnapshot> requireActiveProduct(UUID productId) {
        return productSnapshotRepository.findById(productId)
                .filter(ProductSnapshot::isActive)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no existe o está inactivo")));
    }

    private InventoryMovementResponse toResponse(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.getId(),
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
        );
    }

    private String resolveUsername(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("username");
        }
        return "system";
    }
}

