package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.WarehouseDTO;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.model.Warehouse;
import com.warehouse.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public List<WarehouseDTO> getAll() {
        return warehouseRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public WarehouseDTO getById(Long id) {
        return toDto(warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Склад з ID " + id + " не знайдено")));
    }

    @Transactional
    public WarehouseDTO create(WarehouseDTO dto) {
        Warehouse w = toEntity(dto);
        w.setId(null);
        return toDto(warehouseRepository.save(w));
    }

    @Transactional
    public WarehouseDTO update(Long id, WarehouseDTO dto) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Склад з ID " + id + " не знайдено"));
        existing.setName(dto.getName());
        existing.setLocation(dto.getLocation());
        existing.setCapacity(dto.getCapacity());
        return toDto(warehouseRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Склад з ID " + id + " не знайдено");
        }
        warehouseRepository.deleteById(id);
    }

    private WarehouseDTO toDto(Warehouse w) {
        return new WarehouseDTO(w.getId(), w.getName(), w.getLocation(), w.getCapacity());
    }

    private Warehouse toEntity(WarehouseDTO d) {
        return new Warehouse(d.getId(), d.getName(), d.getLocation(), d.getCapacity());
    }
}
