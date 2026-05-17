package com.warehouse.supply.client;

import com.warehouse.supply.dto.ProductDTO;
import com.warehouse.supply.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    @PatchMapping("/api/stocks/adjust")
    StockDTO adjustStock(@RequestParam("productId") Long productId,
                         @RequestParam("warehouseId") Long warehouseId,
                         @RequestParam("delta") Double delta);
}
