package com.warehouse.inventory.config;

import com.warehouse.inventory.model.Batch;
import com.warehouse.inventory.model.Product;
import com.warehouse.inventory.model.Stock;
import com.warehouse.inventory.model.Warehouse;
import com.warehouse.inventory.repository.BatchRepository;
import com.warehouse.inventory.repository.ProductRepository;
import com.warehouse.inventory.repository.StockRepository;
import com.warehouse.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Заповнює БД початковими даними ТІЛЬКИ якщо вона порожня.
 * При файловому H2 з ddl-auto=update це означає: seed виконається при першому
 * запуску, а при наступних — пропуститься, тому користувацькі зміни не затираються.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final BatchRepository batchRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (warehouseRepository.count() > 0 || productRepository.count() > 0) {
            log.info("DataInitializer: БД не порожня — seed пропущено.");
            return;
        }
        log.info("DataInitializer: БД порожня — заповнюємо початковими даними...");

        Warehouse w1 = warehouseRepository.save(new Warehouse(null,
                "Центральний склад", "Київ, вул. Промислова 1", 10000.0));
        Warehouse w2 = warehouseRepository.save(new Warehouse(null,
                "Склад №2 Харків",   "Харків, вул. Індустріальна 5", 7500.0));
        Warehouse w3 = warehouseRepository.save(new Warehouse(null,
                "Склад №3 Одеса",    "Одеса, вул. Портова 12",       5000.0));

        Product p1 = productRepository.save(new Product(null,
                "Пшениця озима", "тонна", 50.0, "Озима пшениця 1-го класу"));
        Product p2 = productRepository.save(new Product(null,
                "Кукурудза",     "тонна", 30.0, "Кукурудза вологістю до 14%"));
        Product p3 = productRepository.save(new Product(null,
                "Соняшник",      "тонна", 20.0, "Насіння соняшника"));
        Product p4 = productRepository.save(new Product(null,
                "Ячмінь",        "тонна", 25.0, "Ячмінь пивоварний"));
        Product p5 = productRepository.save(new Product(null,
                "Ріпак",         "тонна", 10.0, "Насіння ріпаку"));

        LocalDateTime now = LocalDateTime.now();
        stockRepository.save(new Stock(null, p1.getId(), w1.getId(), 200.0, now));
        stockRepository.save(new Stock(null, p2.getId(), w1.getId(), 150.0, now));
        stockRepository.save(new Stock(null, p3.getId(), w2.getId(),  80.0, now));
        stockRepository.save(new Stock(null, p4.getId(), w2.getId(), 100.0, now));
        stockRepository.save(new Stock(null, p5.getId(), w3.getId(),  45.0, now));
        stockRepository.save(new Stock(null, p1.getId(), w3.getId(),  60.0, now));

        // supplier_id посилається на supply-service (передбачається seed: 1,2,3)
        batchRepository.save(new Batch(null, p1.getId(), 1L, 200.0,
                LocalDate.of(2026, 3, 15), LocalDate.of(2027, 3, 15)));
        batchRepository.save(new Batch(null, p2.getId(), 1L, 150.0,
                LocalDate.of(2026, 3, 16), LocalDate.of(2027, 3, 16)));
        batchRepository.save(new Batch(null, p3.getId(), 2L,  80.0,
                LocalDate.of(2026, 3, 18), LocalDate.of(2027, 6, 18)));
        batchRepository.save(new Batch(null, p4.getId(), 3L, 100.0,
                LocalDate.of(2026, 3, 20), LocalDate.of(2027, 3, 20)));

        log.info("DataInitializer: завантажено {} складів, {} товарів, {} залишків, {} партій.",
                warehouseRepository.count(),
                productRepository.count(),
                stockRepository.count(),
                batchRepository.count());
    }
}
