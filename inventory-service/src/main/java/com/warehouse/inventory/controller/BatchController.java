package com.warehouse.inventory.controller;

import com.warehouse.inventory.dto.BatchDTO;
import com.warehouse.inventory.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService batchService;

    @GetMapping
    public List<BatchDTO> getAll() { return batchService.getAll(); }

    @GetMapping("/{id}")
    public BatchDTO getById(@PathVariable Long id) { return batchService.getById(id); }

    @GetMapping("/by-product/{productId}")
    public List<BatchDTO> getByProduct(@PathVariable Long productId) {
        return batchService.getByProductId(productId);
    }

    @GetMapping("/by-supplier/{supplierId}")
    public List<BatchDTO> getBySupplier(@PathVariable Long supplierId) {
        return batchService.getBySupplierId(supplierId);
    }

    @PostMapping
    public ResponseEntity<BatchDTO> create(@RequestBody BatchDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.create(dto));
    }

    @PutMapping("/{id}")
    public BatchDTO update(@PathVariable Long id, @RequestBody BatchDTO dto) {
        return batchService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        batchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
