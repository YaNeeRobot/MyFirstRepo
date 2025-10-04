-- Справочник статусов заказов
CREATE TABLE IF NOT EXISTS order_status (
                                            id SERIAL PRIMARY KEY,
                                            name VARCHAR(50) NOT NULL UNIQUE
    );

COMMENT ON TABLE order_status IS 'Справочник статусов заказов';
COMMENT ON COLUMN order_status.id IS 'Уникальный идентификатор статуса';
COMMENT ON COLUMN order_status.name IS 'Название статуса (например: "новый", "оплачен", "отменён")';

-- Клиенты
CREATE TABLE IF NOT EXISTS customer (
                                        id SERIAL PRIMARY KEY,
                                        first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(150) UNIQUE
    );

COMMENT ON TABLE customer IS 'Клиенты интернет-магазина';
COMMENT ON COLUMN customer.id IS 'Уникальный идентификатор клиента';
COMMENT ON COLUMN customer.first_name IS 'Имя клиента';
COMMENT ON COLUMN customer.last_name IS 'Фамилия клиента';
COMMENT ON COLUMN customer.phone IS 'Номер телефона';
COMMENT ON COLUMN customer.email IS 'Электронная почта (уникальная)';

-- Товары
CREATE TABLE IF NOT EXISTS product (
                                       id SERIAL PRIMARY KEY,
                                       description TEXT NOT NULL,
                                       price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    quantity_in_stock INT NOT NULL CHECK (quantity_in_stock >= 0),
    category VARCHAR(100) NOT NULL
    );

COMMENT ON TABLE product IS 'Товары в каталоге';
COMMENT ON COLUMN product.id IS 'Уникальный идентификатор товара';
COMMENT ON COLUMN product.description IS 'Описание товара';
COMMENT ON COLUMN product.price IS 'Цена товара (в рублях или другой валюте)';
COMMENT ON COLUMN product.quantity_in_stock IS 'Количество на складе';
COMMENT ON COLUMN product.category IS 'Категория товара (например: "электроника", "книги")';

-- Заказы
CREATE TABLE IF NOT EXISTS "order" (
                                       id SERIAL PRIMARY KEY,
                                       product_id INT NOT NULL REFERENCES product(id) ON DELETE RESTRICT,
    customer_id INT NOT NULL REFERENCES customer(id) ON DELETE RESTRICT,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    quantity INT NOT NULL CHECK (quantity > 0),
    status_id INT NOT NULL REFERENCES order_status(id) ON DELETE RESTRICT
    );

COMMENT ON TABLE "order" IS 'Заказы клиентов';
COMMENT ON COLUMN "order".id IS 'Уникальный идентификатор заказа';
COMMENT ON COLUMN "order".product_id IS 'Ссылка на товар';
COMMENT ON COLUMN "order".customer_id IS 'Ссылка на клиента';
COMMENT ON COLUMN "order".order_date IS 'Дата оформления заказа';
COMMENT ON COLUMN "order".quantity IS 'Количество единиц товара в заказе';
COMMENT ON COLUMN "order".status_id IS 'Текущий статус заказа';

-- Индексы
CREATE INDEX IF NOT EXISTS idx_order_product_id ON "order" (product_id);
CREATE INDEX IF NOT EXISTS idx_order_customer_id ON "order" (customer_id);
CREATE INDEX IF NOT EXISTS idx_order_order_date ON "order" (order_date);

-- Заполнение справочника статусов
INSERT INTO order_status (name) VALUES
                                    ('новый'),
                                    ('оплачен'),
                                    ('в обработке'),
                                    ('отправлен'),
                                    ('доставлен'),
                                    ('отменён')
    ON CONFLICT (name) DO NOTHING;

-- Тестовые клиенты (10+)
INSERT INTO customer (first_name, last_name, phone, email) VALUES
                                                               ('Иван', 'Иванов', '+79001112233', 'ivan@example.com'),
                                                               ('Мария', 'Петрова', '+79002223344', 'maria@example.com'),
                                                               ('Алексей', 'Сидоров', '+79003334455', 'alex@example.com'),
                                                               ('Елена', 'Кузнецова', '+79004445566', 'elena@example.com'),
                                                               ('Дмитрий', 'Смирнов', '+79005556677', 'dmitry@example.com'),
                                                               ('Ольга', 'Попова', '+79006667788', 'olga@example.com'),
                                                               ('Сергей', 'Волков', '+79007778899', 'sergey@example.com'),
                                                               ('Анна', 'Морозова', '+79008889900', 'anna@example.com'),
                                                               ('Павел', 'Новиков', '+79009990011', 'pavel@example.com'),
                                                               ('Татьяна', 'Лебедева', '+79000001122', 'tanya@example.com'),
                                                               ('Артём', 'Зайцев', '+79001112244', 'artem@example.com')
    ON CONFLICT (email) DO NOTHING;

-- Тестовые товары (10+)
INSERT INTO product (description, price, quantity_in_stock, category) VALUES
                                                                          ('Смартфон XYZ', 25000.00, 50, 'Электроника'),
                                                                          ('Наушники беспроводные', 3500.00, 120, 'Электроника'),
                                                                          ('Книга "SQL для начинающих"', 800.00, 200, 'Книги'),
                                                                          ('Футболка хлопковая', 1200.00, 300, 'Одежда'),
                                                                          ('Часы наручные', 7500.00, 40, 'Аксессуары'),
                                                                          ('Планшет ABC', 18000.00, 30, 'Электроника'),
                                                                          ('Кружка керамическая', 450.00, 500, 'Посуда'),
                                                                          ('Рюкзак городской', 2200.00, 80, 'Аксессуары'),
                                                                          ('Кроссовки спортивные', 4500.00, 60, 'Обувь'),
                                                                          ('Настольная лампа', 1600.00, 70, 'Интерьер'),
                                                                          ('USB-флешка 64 ГБ', 900.00, 150, 'Электроника')
    ON CONFLICT DO NOTHING;

-- Тестовые заказы (10+)
INSERT INTO "order" (product_id, customer_id, order_date, quantity, status_id) VALUES
                                                                                   (1, 1, '2025-09-01', 1, 4),  -- доставлен
                                                                                   (2, 2, '2025-09-05', 2, 3),  -- отправлен
                                                                                   (3, 3, '2025-09-10', 1, 2),  -- в обработке
                                                                                   (4, 4, '2025-09-12', 3, 1),  -- новый
                                                                                   (5, 5, '2025-09-15', 1, 5),  -- отменён
                                                                                   (6, 6, '2025-09-18', 1, 4),  -- доставлен
                                                                                   (7, 7, '2025-09-20', 5, 2),  -- в обработке
                                                                                   (8, 8, '2025-09-22', 1, 3),  -- отправлен
                                                                                   (9, 9, '2025-09-25', 1, 4),  -- доставлен
                                                                                   (10, 10, '2025-09-28', 2, 1), -- новый
                                                                                   (11, 11, '2025-10-01', 1, 2)  -- в обработке
    ON CONFLICT DO NOTHING;