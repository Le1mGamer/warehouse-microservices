package com.warehouse.inventory.client;

import com.warehouse.inventory.dto.SupplierDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "supply-service", url = "${supply-service.url}")
public interface SupplierClient {

    @GetMapping("/api/suppliers/{id}")
    SupplierDTO getSupplierById(@PathVariable("id") Long id);
}
