package com.warehouse.supply.config;

import com.warehouse.supply.model.Supplier;
import com.warehouse.supply.model.SupplyOrder;
import com.warehouse.supply.model.enums.OrderStatus;
import com.warehouse.supply.repository.SupplierRepository;
import com.warehouse.supply.repository.SupplyOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Заповнює БД початковими даними ТІЛЬКИ якщо вона порожня.
 * При файловому H2 з ddl-auto=update це означає: seed виконається при першому
 * запуску, а при наступних — пропуститься, тому користувацькі зміни не затираються.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final SupplyOrderRepository supplyOrderRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (supplierRepository.count() > 0) {
            log.info("DataInitializer: БД не порожня — seed пропущено.");
            return;
        }
        log.info("DataInitializer: БД порожня — заповнюємо початковими даними...");

        Supplier s1 = supplierRepository.save(new Supplier(null,
                "ТОВ АгроІмпорт",   "Іваненко Іван",     "+380501234567",
                "ivan@agro.ua",     "Харків, вул. Торгова 10"));
        Supplier s2 = supplierRepository.save(new Supplier(null,
                "ФГ Зерновий Край", "Петренко Петро",    "+380671234567",
                "petro@zerno.ua",   "Полтава, вул. Аграрна 3"));
        Supplier s3 = supplierRepository.save(new Supplier(null,
                "ПП Овочевий Світ", "Сидоренко Олексій", "+380631234567",
                "oleksiy@ovoch.ua", "Вінниця, вул. Садова 7"));

        // product_id, warehouse_id посилаються на inventory-service (seed: 1..5, 1..3)
        supplyOrderRepository.save(new SupplyOrder(null, s1.getId(), 1L, 500.0, 1L,
                LocalDate.of(2026, 3, 1),  LocalDate.of(2026, 3, 15), OrderStatus.DELIVERED));
        supplyOrderRepository.save(new SupplyOrder(null, s1.getId(), 2L, 300.0, 1L,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 25), OrderStatus.SHIPPED));
        supplyOrderRepository.save(new SupplyOrder(null, s2.getId(), 3L, 200.0, 2L,
                LocalDate.of(2026, 3, 20), LocalDate.of(2026, 4, 5),  OrderStatus.CONFIRMED));
        supplyOrderRepository.save(new SupplyOrder(null, s3.getId(), 4L, 150.0, 2L,
                LocalDate.of(2026, 3, 28), LocalDate.of(2026, 4, 15), OrderStatus.CREATED));
        supplyOrderRepository.save(new SupplyOrder(null, s2.getId(), 5L, 100.0, 3L,
                LocalDate.of(2026, 3, 29), LocalDate.of(2026, 4, 20), OrderStatus.CREATED));

        log.info("DataInitializer: завантажено {} постачальників, {} замовлень.",
                supplierRepository.count(),
                supplyOrderRepository.count());
    }
}
