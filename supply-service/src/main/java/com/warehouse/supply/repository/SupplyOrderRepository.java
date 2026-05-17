package com.warehouse.supply.repository;

import com.warehouse.supply.model.SupplyOrder;
import com.warehouse.supply.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {
    List<SupplyOrder> findBySupplierId(Long supplierId);
    List<SupplyOrder> findByStatus(OrderStatus status);
}
