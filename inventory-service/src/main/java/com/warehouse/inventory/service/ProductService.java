package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.ProductDTO;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.model.Product;
import com.warehouse.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "products", key = "#id")
    public ProductDTO getById(Long id) {
        simulateSlowDatabaseRequest();

        return toDto(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар з ID " + id + " не знайдено")));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO create(ProductDTO dto) {
        Product p = toEntity(dto);
        p.setId(null);
        return toDto(productRepository.save(p));
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар з ID " + id + " не знайдено"));
        existing.setName(dto.getName());
        existing.setUnit(dto.getUnit());
        existing.setMinStock(dto.getMinStock());
        existing.setDescription(dto.getDescription());
        return toDto(productRepository.save(existing));
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Товар з ID " + id + " не знайдено");
        }
        productRepository.deleteById(id);
    }

    private void simulateSlowDatabaseRequest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private ProductDTO toDto(Product p) {
        return new ProductDTO(p.getId(), p.getName(), p.getUnit(), p.getMinStock(), p.getDescription());
    }

    private Product toEntity(ProductDTO d) {
        return new Product(d.getId(), d.getName(), d.getUnit(), d.getMinStock(), d.getDescription());
    }
}
