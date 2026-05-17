package com.warehouse.inventory.repository;

import com.warehouse.inventory.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByProductId(Long productId);
    List<Stock> findByWarehouseId(Long warehouseId);
    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
