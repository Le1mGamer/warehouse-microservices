package com.warehouse.inventory.controller;

import com.warehouse.inventory.dto.WarehouseDTO;
import com.warehouse.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping
    public List<WarehouseDTO> getAll() { return warehouseService.getAll(); }

    @GetMapping("/{id}")
    public WarehouseDTO getById(@PathVariable Long id) { return warehouseService.getById(id); }

    @PostMapping
    public ResponseEntity<WarehouseDTO> create(@RequestBody WarehouseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(dto));
    }

    @PutMapping("/{id}")
    public WarehouseDTO update(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        return warehouseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
