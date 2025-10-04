-- =============================================
-- 📖 5 ЗАПРОСОВ НА ЧТЕНИЕ
-- =============================================

-- 1. Список всех заказов за последние 7 дней с именем покупателя и описанием товара
SELECT
    o.id AS order_id,
    c.first_name || ' ' || c.last_name AS customer_name,
    p.description AS product_description,
    o.quantity,
    o.order_date
FROM "order" o
         JOIN customer c ON o.customer_id = c.id
         JOIN product p ON o.product_id = p.id
WHERE o.order_date >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY o.order_date DESC;

-- 2. Топ-3 самых популярных товаров (по количеству проданных единиц)
SELECT
    p.description,
    SUM(o.quantity) AS total_sold
FROM "order" o
         JOIN product p ON o.product_id = p.id
GROUP BY p.id, p.description
ORDER BY total_sold DESC
    LIMIT 3;

-- 3. Список клиентов, у которых есть заказы со статусом "доставлен"
SELECT DISTINCT
    c.first_name,
    c.last_name,
    c.email
FROM customer c
         JOIN "order" o ON c.id = o.customer_id
         JOIN order_status s ON o.status_id = s.id
WHERE s.name = 'доставлен'
ORDER BY c.last_name;

-- 4. Общая выручка по категориям за всё время
SELECT
    p.category,
    SUM(p.price * o.quantity) AS total_revenue
FROM "order" o
         JOIN product p ON o.product_id = p.id
         JOIN order_status s ON o.status_id = s.id
WHERE s.name NOT IN ('отменён')  -- исключаем отменённые заказы
GROUP BY p.category
ORDER BY total_revenue DESC;

-- 5. Заказы с количеством > 1 и статусом "новый" или "в обработке"
SELECT
    o.id,
    c.first_name,
    p.description,
    o.quantity,
    s.name AS status
FROM "order" o
         JOIN customer c ON o.customer_id = c.id
         JOIN product p ON o.product_id = p.id
         JOIN order_status s ON o.status_id = s.id
WHERE o.quantity > 1
  AND s.name IN ('новый', 'в обработке')
ORDER BY o.order_date;

-- =============================================
-- ✏️ 3 ЗАПРОСА НА ИЗМЕНЕНИЕ (UPDATE)
-- =============================================

-- 6. Уменьшить количество товара на складе после оформления заказа (пример: заказ №1)
UPDATE product
SET quantity_in_stock = quantity_in_stock - (
    SELECT quantity FROM "order" WHERE id = 1
)
WHERE id = (SELECT product_id FROM "order" WHERE id = 1)
  AND quantity_in_stock >= (SELECT quantity FROM "order" WHERE id = 1);

-- 7. Обновить статус всех заказов клиента с email 'ivan@example.com' на "оплачен"
UPDATE "order"
SET status_id = (SELECT id FROM order_status WHERE name = 'оплачен')
WHERE customer_id = (SELECT id FROM customer WHERE email = 'ivan@example.com');

-- 8. Увеличить цену всех товаров в категории "Электроника" на 10%
UPDATE product
SET price = price * 1.10
WHERE category = 'Электроника';

-- =============================================
-- 🗑️ 2 ЗАПРОСА НА УДАЛЕНИЕ (DELETE)
-- =============================================

-- 9. Удалить клиентов, у которых нет ни одного заказа
DELETE FROM customer
WHERE id NOT IN (SELECT DISTINCT customer_id FROM "order");

-- 10. Удалить все отменённые заказы старше 30 дней
DELETE FROM "order"
WHERE status_id = (SELECT id FROM order_status WHERE name = 'отменён')
  AND order_date < CURRENT_DATE - INTERVAL '30 days';