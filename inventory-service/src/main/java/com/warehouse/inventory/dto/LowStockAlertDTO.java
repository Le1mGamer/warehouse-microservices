package com.warehouse.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertDTO {
    private Long stockId;
    private Long productId;
    private String productName;
    private String unit;
    private Long warehouseId;
    private String warehouseName;
    private Double currentQuantity;
    private Double minStock;
    private Double deficit;
    private String severity;
}
