package com.warehouse.supply.dto;

import com.warehouse.supply.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrderDTO {
    private Long id;
    private Long supplierId;
    private Long productId;
    private Double quantity;
    private Long warehouseId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private OrderStatus status;
}
