package com.warehouse.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {
    private Long id;
    private Long productId;
    private Long supplierId;
    private Double quantity;
    private LocalDate arrivalDate;
    private LocalDate expiryDate;
}
