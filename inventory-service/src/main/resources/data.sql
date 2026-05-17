-- ==========================================
-- СКЛАДИ (Warehouses)
-- ==========================================
INSERT INTO warehouses (name, location, capacity) VALUES ('Центральний склад', 'Київ, вул. Промислова 1', 10000.0);
INSERT INTO warehouses (name, location, capacity) VALUES ('Склад №2 Харків', 'Харків, вул. Індустріальна 5', 7500.0);
INSERT INTO warehouses (name, location, capacity) VALUES ('Склад №3 Одеса', 'Одеса, вул. Портова 12', 5000.0);

-- ==========================================
-- ТОВАРИ (Products)
-- ==========================================
INSERT INTO products (name, unit, min_stock, description) VALUES ('Пшениця озима', 'тонна', 50.0, 'Озима пшениця 1-го класу');
INSERT INTO products (name, unit, min_stock, description) VALUES ('Кукурудза', 'тонна', 30.0, 'Кукурудза вологістю до 14%');
INSERT INTO products (name, unit, min_stock, description) VALUES ('Соняшник', 'тонна', 20.0, 'Насіння соняшника');
INSERT INTO products (name, unit, min_stock, description) VALUES ('Ячмінь', 'тонна', 25.0, 'Ячмінь пивоварний');
INSERT INTO products (name, unit, min_stock, description) VALUES ('Ріпак', 'тонна', 10.0, 'Насіння ріпаку');

-- ==========================================
-- ЗАЛИШКИ (Stocks)
-- ==========================================
INSERT INTO stocks (product_id, warehouse_id, quantity, last_updated) VALUES (1, 1, 200.0, CURRENT_TIMESTAMP);
INSERT INTO stocks (product_id, warehouse_id, quantity, last_updated) VALUES (2, 1, 150.0, CURRENT_TIMESTAMP);
INSERT INTO stocks (product_id, warehouse_id, quantity, last_updated) VALUES (3, 2, 80.0, CURRENT_TIMESTAMP);
INSERT INTO stocks (product_id, warehouse_id, quantity, last_updated) VALUES (4, 2, 100.0, CURRENT_TIMESTAMP);
INSERT INTO stocks (product_id, warehouse_id, quantity, last_updated) VALUES (5, 3, 45.0, CURRENT_TIMESTAMP);
INSERT INTO stocks (product_id, warehouse_id, quantity, last_updated) VALUES (1, 3, 60.0, CURRENT_TIMESTAMP);

-- ==========================================
-- ПАРТІЇ (Batches) — supplier_id зберігається як зовнішнє посилання на supply-service
-- ==========================================
INSERT INTO batches (product_id, supplier_id, quantity, arrival_date, expiry_date)
VALUES (1, 1, 200.0, '2026-03-15', '2027-03-15');
INSERT INTO batches (product_id, supplier_id, quantity, arrival_date, expiry_date)
VALUES (2, 1, 150.0, '2026-03-16', '2027-03-16');
INSERT INTO batches (product_id, supplier_id, quantity, arrival_date, expiry_date)
VALUES (3, 2, 80.0, '2026-03-18', '2027-06-18');
INSERT INTO batches (product_id, supplier_id, quantity, arrival_date, expiry_date)
VALUES (4, 3, 100.0, '2026-03-20', '2027-03-20');
