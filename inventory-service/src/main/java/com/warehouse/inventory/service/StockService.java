package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.LowStockAlertDTO;
import com.warehouse.inventory.dto.StockDTO;
import com.warehouse.inventory.exception.BusinessLogicException;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.model.Product;
import com.warehouse.inventory.model.Stock;
import com.warehouse.inventory.model.Warehouse;
import com.warehouse.inventory.repository.ProductRepository;
import com.warehouse.inventory.repository.StockRepository;
import com.warehouse.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public List<StockDTO> getAll() {
        return stockRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public StockDTO getById(Long id) {
        return toDto(stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Залишок з ID " + id + " не знайдено")));
    }

    public List<StockDTO> getByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Товар з ID " + productId + " не знайдено");
        }
        return stockRepository.findByProductId(productId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<StockDTO> getByWarehouseId(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Склад з ID " + warehouseId + " не знайдено");
        }
        return stockRepository.findByWarehouseId(warehouseId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public StockDTO create(StockDTO dto) {
        if (!productRepository.existsById(dto.getProductId())) {
            throw new ResourceNotFoundException("Товар з ID " + dto.getProductId() + " не знайдено");
        }
        if (!warehouseRepository.existsById(dto.getWarehouseId())) {
            throw new ResourceNotFoundException("Склад з ID " + dto.getWarehouseId() + " не знайдено");
        }
        if (stockRepository.findByProductIdAndWarehouseId(dto.getProductId(), dto.getWarehouseId()).isPresent()) {
            throw new BusinessLogicException("Запис про залишок для цього товару на вказаному складі вже існує");
        }
        Stock stock = new Stock(null, dto.getProductId(), dto.getWarehouseId(),
                dto.getQuantity(), LocalDateTime.now());
        return toDto(stockRepository.save(stock));
    }

    @Transactional
    public StockDTO update(Long id, StockDTO dto) {
        Stock existing = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Залишок з ID " + id + " не знайдено"));
        existing.setQuantity(dto.getQuantity());
        existing.setLastUpdated(LocalDateTime.now());
        return toDto(stockRepository.save(existing));
    }

    @Transactional
    public StockDTO adjustQuantity(Long productId, Long warehouseId, Double delta) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Залишок для товару " + productId + " на складі " + warehouseId + " не знайдено"));
        double newQty = stock.getQuantity() + delta;
        if (newQty < 0) {
            throw new BusinessLogicException("Недостатньо товару на складі. Доступно: " + stock.getQuantity());
        }
        stock.setQuantity(newQty);
        stock.setLastUpdated(LocalDateTime.now());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Товар з ID " + productId + " не знайдено"));
        if (newQty < product.getMinStock()) {
            System.out.println("УВАГА: Залишок товару " + product.getName() + " нижче мінімального рівня!");
        }
        return toDto(stockRepository.save(stock));
    }

    /**
     * Контроль мінімального залишку: повертає всі позиції, де
     * поточна кількість на складі менша або дорівнює minStock товару.
     * Опційні фільтри — productId, warehouseId.
     */
    public List<LowStockAlertDTO> getLowStockAlerts(Long productId, Long warehouseId) {
        List<Stock> stocks;
        if (productId != null && warehouseId != null) {
            stocks = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                    .map(List::of).orElse(List.of());
        } else if (productId != null) {
            stocks = stockRepository.findByProductId(productId);
        } else if (warehouseId != null) {
            stocks = stockRepository.findByWarehouseId(warehouseId);
        } else {
            stocks = stockRepository.findAll();
        }

        List<LowStockAlertDTO> alerts = new ArrayList<>();
        for (Stock s : stocks) {
            Product p = productRepository.findById(s.getProductId()).orElse(null);
            if (p == null) continue;
            if (s.getQuantity() <= p.getMinStock()) {
                Warehouse w = warehouseRepository.findById(s.getWarehouseId()).orElse(null);
                LowStockAlertDTO a = new LowStockAlertDTO();
                a.setStockId(s.getId());
                a.setProductId(p.getId());
                a.setProductName(p.getName());
                a.setUnit(p.getUnit());
                a.setWarehouseId(s.getWarehouseId());
                a.setWarehouseName(w != null ? w.getName() : null);
                a.setCurrentQuantity(s.getQuantity());
                a.setMinStock(p.getMinStock());
                a.setDeficit(p.getMinStock() - s.getQuantity());
                a.setSeverity(s.getQuantity() == 0.0 ? "EMPTY" : "BELOW_MIN");
                alerts.add(a);
            }
        }
        return alerts;
    }

    @Transactional
    public void delete(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("Залишок з ID " + id + " не знайдено");
        }
        stockRepository.deleteById(id);
    }

    private StockDTO toDto(Stock s) {
        return new StockDTO(s.getId(), s.getProductId(), s.getWarehouseId(), s.getQuantity(), s.getLastUpdated());
    }
}
