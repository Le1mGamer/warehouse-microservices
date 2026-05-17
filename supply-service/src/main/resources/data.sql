-- ==========================================
-- ПОСТАЧАЛЬНИКИ (Suppliers)
-- ==========================================
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES ('ТОВ АгроІмпорт', 'Іваненко Іван', '+380501234567', 'ivan@agro.ua', 'Харків, вул. Торгова 10');
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES ('ФГ Зерновий Край', 'Петренко Петро', '+380671234567', 'petro@zerno.ua', 'Полтава, вул. Аграрна 3');
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES ('ПП Овочевий Світ', 'Сидоренко Олексій', '+380631234567', 'oleksiy@ovoch.ua', 'Вінниця, вул. Садова 7');

-- ==========================================
-- ЗАМОВЛЕННЯ НА ПОСТАВКУ (Supply Orders)
-- product_id посилається на товар у inventory-service
-- ==========================================
INSERT INTO supply_orders (supplier_id, product_id, quantity, warehouse_id, order_date, expected_delivery_date, status)
VALUES (1, 1, 500.0, 1, '2026-03-01', '2026-03-15', 'DELIVERED');
INSERT INTO supply_orders (supplier_id, product_id, quantity, warehouse_id, order_date, expected_delivery_date, status)
VALUES (1, 2, 300.0, 1, '2026-03-10', '2026-03-25', 'SHIPPED');
INSERT INTO supply_orders (supplier_id, product_id, quantity, warehouse_id, order_date, expected_delivery_date, status)
VALUES (2, 3, 200.0, 2, '2026-03-20', '2026-04-05', 'CONFIRMED');
INSERT INTO supply_orders (supplier_id, product_id, quantity, warehouse_id, order_date, expected_delivery_date, status)
VALUES (3, 4, 150.0, 2, '2026-03-28', '2026-04-15', 'CREATED');
INSERT INTO supply_orders (supplier_id, product_id, quantity, warehouse_id, order_date, expected_delivery_date, status)
VALUES (2, 5, 100.0, 3, '2026-03-29', '2026-04-20', 'CREATED');
