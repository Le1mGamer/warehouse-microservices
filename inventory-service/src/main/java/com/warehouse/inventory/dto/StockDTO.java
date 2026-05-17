package com.warehouse.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private Double quantity;
    private LocalDateTime lastUpdated;
}
