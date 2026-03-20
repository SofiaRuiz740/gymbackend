package com.gym.inventory.repository;

import com.gym.inventory.domain.StockItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface StockItemRepository extends ReactiveCrudRepository<StockItem, UUID> {
}

