package com.warehouse.supply.service;

import com.warehouse.supply.client.InventoryClient;
import com.warehouse.supply.dto.ProductDTO;
import com.warehouse.supply.dto.SupplyOrderDTO;
import com.warehouse.supply.exception.BusinessLogicException;
import com.warehouse.supply.exception.ResourceNotFoundException;
import com.warehouse.supply.exception.ServiceUnavailableException;
import com.warehouse.supply.model.SupplyOrder;
import com.warehouse.supply.model.enums.OrderStatus;
import com.warehouse.supply.repository.SupplierRepository;
import com.warehouse.supply.repository.SupplyOrderRepository;
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
public class SupplyOrderService {
    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryClient inventoryClient;

    public List<SupplyOrderDTO> getAll() {
        return supplyOrderRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public SupplyOrderDTO getById(Long id) {
        return toDto(supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Замовлення з ID " + id + " не знайдено")));
    }

    public List<SupplyOrderDTO> getBySupplierId(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Постачальника з ID " + supplierId + " не знайдено");
        }
        return supplyOrderRepository.findBySupplierId(supplierId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SupplyOrderDTO> getByStatus(OrderStatus status) {
        return supplyOrderRepository.findByStatus(status).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public SupplyOrderDTO create(SupplyOrderDTO dto) {
        if (!supplierRepository.existsById(dto.getSupplierId())) {
            throw new ResourceNotFoundException("Постачальника з ID " + dto.getSupplierId() + " не знайдено");
        }
        validateProductExists(dto.getProductId());

        SupplyOrder order = new SupplyOrder(null, dto.getSupplierId(), dto.getProductId(),
                dto.getQuantity(), dto.getWarehouseId(), dto.getOrderDate(),
                dto.getExpectedDeliveryDate(),
                dto.getStatus() == null ? OrderStatus.CREATED : dto.getStatus());
        return toDto(supplyOrderRepository.save(order));
    }

    @Transactional
    public SupplyOrderDTO update(Long id, SupplyOrderDTO dto) {
        SupplyOrder existing = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Замовлення з ID " + id + " не знайдено"));
        if (!supplierRepository.existsById(dto.getSupplierId())) {
            throw new ResourceNotFoundException("Постачальника з ID " + dto.getSupplierId() + " не знайдено");
        }
        validateProductExists(dto.getProductId());
        existing.setSupplierId(dto.getSupplierId());
        existing.setProductId(dto.getProductId());
        existing.setQuantity(dto.getQuantity());
        existing.setWarehouseId(dto.getWarehouseId());
        existing.setOrderDate(dto.getOrderDate());
        existing.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        existing.setStatus(dto.getStatus());
        return toDto(supplyOrderRepository.save(existing));
    }

    @Transactional
    public SupplyOrderDTO updateStatus(Long id, OrderStatus newStatus) {
        SupplyOrder order = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Замовлення з ID " + id + " не знайдено"));
        OrderStatus prev = order.getStatus();
        order.setStatus(newStatus);
        SupplyOrder saved = supplyOrderRepository.save(order);

        // Бізнес-сценарій: коли замовлення стало DELIVERED — оновлюємо залишки в inventory-service
        if (newStatus == OrderStatus.DELIVERED && prev != OrderStatus.DELIVERED) {
            if (order.getWarehouseId() == null) {
                throw new BusinessLogicException(
                        "Неможливо завершити постачання: для замовлення " + id + " не вказано склад (warehouse_id).");
            }
            try {
                inventoryClient.adjustStock(order.getProductId(), order.getWarehouseId(), order.getQuantity());
                log.info("Залишки оновлено: товар {} склад {} +{}",
                        order.getProductId(), order.getWarehouseId(), order.getQuantity());
            } catch (FeignException e) {
                log.error("Feign error під час оновлення залишків: status={}, body={}",
                        e.status(), e.contentUTF8(), e);
                throw new ServiceUnavailableException(
                        "Сервіс інвентаря недоступний — не вдалося оновити залишки для замовлення " + id, e);
            } catch (Exception e) {
                log.error("Помилка зв'язку з inventory-service", e);
                throw new ServiceUnavailableException(
                        "Помилка зв'язку з inventory-service: " + e.getMessage(), e);
            }
        }
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!supplyOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Замовлення з ID " + id + " не знайдено");
        }
        supplyOrderRepository.deleteById(id);
    }

    private void validateProductExists(Long productId) {
        try {
            ProductDTO product = inventoryClient.getProductById(productId);
            log.info("Перевірено існування товару: {} (id={})", product.getName(), product.getId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Товар з ID " + productId + " не знайдено (inventory-service)");
        } catch (FeignException e) {
            throw new ServiceUnavailableException(
                    "Сервіс інвентаря недоступний. Не вдалося перевірити товар " + productId, e);
        } catch (Exception e) {
            throw new ServiceUnavailableException(
                    "Помилка зв'язку з inventory-service: " + e.getMessage(), e);
        }
    }

    private SupplyOrderDTO toDto(SupplyOrder o) {
        return new SupplyOrderDTO(o.getId(), o.getSupplierId(), o.getProductId(),
                o.getQuantity(), o.getWarehouseId(),
                o.getOrderDate(), o.getExpectedDeliveryDate(), o.getStatus());
    }
}
