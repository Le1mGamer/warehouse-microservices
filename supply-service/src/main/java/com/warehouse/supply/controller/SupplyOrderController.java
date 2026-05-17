package com.warehouse.supply.controller;

import com.warehouse.supply.dto.SupplyOrderDTO;
import com.warehouse.supply.model.enums.OrderStatus;
import com.warehouse.supply.service.SupplyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supply-orders")
@RequiredArgsConstructor
public class SupplyOrderController {
    private final SupplyOrderService supplyOrderService;

    @GetMapping
    public List<SupplyOrderDTO> getAll() { return supplyOrderService.getAll(); }

    @GetMapping("/{id}")
    public SupplyOrderDTO getById(@PathVariable Long id) { return supplyOrderService.getById(id); }

    @GetMapping("/by-supplier/{supplierId}")
    public List<SupplyOrderDTO> getBySupplier(@PathVariable Long supplierId) {
        return supplyOrderService.getBySupplierId(supplierId);
    }

    @GetMapping("/by-status/{status}")
    public List<SupplyOrderDTO> getByStatus(@PathVariable OrderStatus status) {
        return supplyOrderService.getByStatus(status);
    }

    @PostMapping
    public ResponseEntity<SupplyOrderDTO> create(@RequestBody SupplyOrderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyOrderService.create(dto));
    }

    @PutMapping("/{id}")
    public SupplyOrderDTO update(@PathVariable Long id, @RequestBody SupplyOrderDTO dto) {
        return supplyOrderService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public SupplyOrderDTO updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return supplyOrderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplyOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
