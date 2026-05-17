package com.warehouse.inventory.controller;

import com.warehouse.inventory.dto.LowStockAlertDTO;
import com.warehouse.inventory.dto.StockDTO;
import com.warehouse.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping
    public List<StockDTO> getAll() { return stockService.getAll(); }

    @GetMapping("/{id}")
    public StockDTO getById(@PathVariable Long id) { return stockService.getById(id); }

    @GetMapping("/by-product/{productId}")
    public List<StockDTO> getByProduct(@PathVariable Long productId) {
        return stockService.getByProductId(productId);
    }

    @GetMapping("/by-warehouse/{warehouseId}")
    public List<StockDTO> getByWarehouse(@PathVariable Long warehouseId) {
        return stockService.getByWarehouseId(warehouseId);
    }

    @PostMapping
    public ResponseEntity<StockDTO> create(@RequestBody StockDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.create(dto));
    }

    @PutMapping("/{id}")
    public StockDTO update(@PathVariable Long id, @RequestBody StockDTO dto) {
        return stockService.update(id, dto);
    }

    @PatchMapping("/adjust")
    public StockDTO adjust(@RequestParam Long productId,
                           @RequestParam Long warehouseId,
                           @RequestParam Double delta) {
        return stockService.adjustQuantity(productId, warehouseId, delta);
    }

    /**
     * Контроль мінімального залишку.
     *   GET /api/stocks/low-stock
     *   GET /api/stocks/low-stock?productId=1
     *   GET /api/stocks/low-stock?warehouseId=2
     *   GET /api/stocks/low-stock?productId=1&warehouseId=2
     */
    @GetMapping("/low-stock")
    public List<LowStockAlertDTO> getLowStockAlerts(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId) {
        return stockService.getLowStockAlerts(productId, warehouseId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
