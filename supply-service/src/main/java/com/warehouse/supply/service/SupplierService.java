package com.warehouse.supply.service;

import com.warehouse.supply.dto.SupplierDTO;
import com.warehouse.supply.exception.ResourceNotFoundException;
import com.warehouse.supply.model.Supplier;
import com.warehouse.supply.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public List<SupplierDTO> getAll() {
        return supplierRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public SupplierDTO getById(Long id) {
        return toDto(supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Постачальника з ID " + id + " не знайдено")));
    }

    @Transactional
    public SupplierDTO create(SupplierDTO dto) {
        Supplier s = toEntity(dto);
        s.setId(null);
        return toDto(supplierRepository.save(s));
    }

    @Transactional
    public SupplierDTO update(Long id, SupplierDTO dto) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Постачальника з ID " + id + " не знайдено"));
        existing.setName(dto.getName());
        existing.setContactPerson(dto.getContactPerson());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setAddress(dto.getAddress());
        return toDto(supplierRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Постачальника з ID " + id + " не знайдено");
        }
        supplierRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return supplierRepository.existsById(id);
    }

    private SupplierDTO toDto(Supplier s) {
        return new SupplierDTO(s.getId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress());
    }

    private Supplier toEntity(SupplierDTO d) {
        return new Supplier(d.getId(), d.getName(), d.getContactPerson(), d.getPhone(), d.getEmail(), d.getAddress());
    }
}
