package com.warehouse.inventory.service;

import com.warehouse.inventory.client.SupplierClient;
import com.warehouse.inventory.dto.BatchDTO;
import com.warehouse.inventory.dto.SupplierDTO;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.exception.ServiceUnavailableException;
import com.warehouse.inventory.model.Batch;
import com.warehouse.inventory.repository.BatchRepository;
import com.warehouse.inventory.repository.ProductRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final SupplierClient supplierClient;

    public List<BatchDTO> getAll() {
        return batchRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public BatchDTO getById(Long id) {
        return toDto(batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Партію з ID " + id + " не знайдено")));
    }

    public List<BatchDTO> getByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Товар з ID " + productId + " не знайдено");
        }
        return batchRepository.findByProductId(productId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BatchDTO> getBySupplierId(Long supplierId) {
        return batchRepository.findBySupplierId(supplierId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public BatchDTO create(BatchDTO dto) {
        if (!productRepository.existsById(dto.getProductId())) {
            throw new ResourceNotFoundException("Товар з ID " + dto.getProductId() + " не знайдено");
        }
        validateSupplierExists(dto.getSupplierId());

        Batch batch = new Batch(null, dto.getProductId(), dto.getSupplierId(),
                dto.getQuantity(), dto.getArrivalDate(), dto.getExpiryDate());
        return toDto(batchRepository.save(batch));
    }

    @Transactional
    public BatchDTO update(Long id, BatchDTO dto) {
        Batch existing = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Партію з ID " + id + " не знайдено"));
        if (!productRepository.existsById(dto.getProductId())) {
            throw new ResourceNotFoundException("Товар з ID " + dto.getProductId() + " не знайдено");
        }
        validateSupplierExists(dto.getSupplierId());
        existing.setProductId(dto.getProductId());
        existing.setSupplierId(dto.getSupplierId());
        existing.setQuantity(dto.getQuantity());
        existing.setArrivalDate(dto.getArrivalDate());
        existing.setExpiryDate(dto.getExpiryDate());
        return toDto(batchRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Партію з ID " + id + " не знайдено");
        }
        batchRepository.deleteById(id);
    }

    private void validateSupplierExists(Long supplierId) {
        try {
            SupplierDTO supplier = supplierClient.getSupplierById(supplierId);
            log.info("Перевірено існування постачальника: {} (id={})", supplier.getName(), supplier.getId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Постачальника з ID " + supplierId + " не знайдено (supply-service)");
        } catch (FeignException e) {
            throw new ServiceUnavailableException(
                    "Сервіс постачальників недоступний. Не вдалося перевірити постачальника " + supplierId, e);
        } catch (Exception e) {
            throw new ServiceUnavailableException(
                    "Помилка зв'язку з supply-service: " + e.getMessage(), e);
        }
    }

    private BatchDTO toDto(Batch b) {
        return new BatchDTO(b.getId(), b.getProductId(), b.getSupplierId(),
                b.getQuantity(), b.getArrivalDate(), b.getExpiryDate());
    }
}
