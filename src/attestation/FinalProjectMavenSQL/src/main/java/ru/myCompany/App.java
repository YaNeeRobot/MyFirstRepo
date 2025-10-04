package ru.myCompany;

import org.flywaydb.core.Flyway;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class App {

    private static String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static String DB_USER = "postgres";
    private static String DB_PASSWORD = "mypassword";

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL JDBC Driver загружен.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Драйвер не найден!");
            e.printStackTrace();
        }
        try {
            // 1. Загрузка свойств из application.properties
            loadProperties();

            // 2. Запуск миграций через Flyway (если используется)
            runFlywayMigrations();

            // 3. Подключение к БД и выполнение CRUD-операций
            executeCRUDOperations();

        } catch (Exception e) {
            System.err.println("Ошибка выполнения программы: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("⚠️ Файл application.properties не найден!");
                return;
            }
            props.load(input);
            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
            System.out.println("✅ Свойства загружены: " + DB_URL);
        }
    }

    private static void runFlywayMigrations() {
        System.out.println("\n🚀 Запуск миграций Flyway...");

        Flyway flyway = Flyway.configure()
                .dataSource(DB_URL, DB_USER, DB_PASSWORD)
                .locations("classpath:db/migration") // путь к миграциям
                .load();

        try {
            flyway.migrate();
            System.out.println("✅ Миграции успешно применены.");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при применении миграций: " + e.getMessage());
        }
    }

    private static void executeCRUDOperations() throws SQLException {
        System.out.println("\n📝 Начинаем CRUD-операции...");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // начало транзакции

            try {
                // 1. Вставка нового товара и покупателя (PreparedStatement)
                insertProductAndCustomer(conn);

                // 2. Создание заказа для покупателя
                createOrder(conn);

                // 3. Чтение и вывод последних 5 заказов с JOIN
                printLatestOrders(conn);

                // 4. Обновление цены товара и количества на складе
                updateProductPriceAndStock(conn);

                // 5. Удаление тестовых записей
                deleteTestRecords(conn);

                conn.commit(); // фиксируем транзакцию
                System.out.println("\n✅ Все операции успешно завершены и зафиксированы.");

            } catch (SQLException e) {
                conn.rollback(); // откатываем при ошибке
                System.err.println("❌ Ошибка в транзакции, откат выполнен: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка подключения к БД: " + e.getMessage());
        }
    }

    private static void insertProductAndCustomer(Connection conn) throws SQLException {
        System.out.println("\n🔹 1. Вставка нового товара и покупателя...");

        String insertCustomer = """
            INSERT INTO customer (first_name, last_name, phone, email)
            VALUES (?, ?, ?, ?)
            RETURNING id;
            """;

        String insertProduct = """
            INSERT INTO product (description, price, quantity_in_stock, category)
            VALUES (?, ?, ?, ?)
            RETURNING id;
            """;

        try (PreparedStatement psCust = conn.prepareStatement(insertCustomer);
             PreparedStatement psProd = conn.prepareStatement(insertProduct)) {

            // Вставляем клиента
            psCust.setString(1, "Иван");
            psCust.setString(2, "Петров");
            psCust.setString(3, "+79001234567");
            psCust.setString(4, "ivan.petrov@example.com");
            ResultSet rsCust = psCust.executeQuery();
            int customerId = 0;
            if (rsCust.next()) customerId = rsCust.getInt(1);

            // Вставляем товар
            psProd.setString(1, "Ноутбук SuperBook");
            psProd.setBigDecimal(2, new java.math.BigDecimal("45000.00"));
            psProd.setInt(3, 10);
            psProd.setString(4, "Электроника");
            ResultSet rsProd = psProd.executeQuery();
            int productId = 0;
            if (rsProd.next()) productId = rsProd.getInt(1);

            System.out.printf("✅ Добавлен клиент ID=%d, товар ID=%d\n", customerId, productId);
        }
    }

    private static void createOrder(Connection conn) throws SQLException {
        System.out.println("\n🔹 2. Создание заказа...");

        String insertOrder = """
            INSERT INTO "order" (product_id, customer_id, order_date, quantity, status_id)
            VALUES (?, ?, CURRENT_DATE, ?, 
                    (SELECT id FROM order_status WHERE name = 'новый'))
            RETURNING id;
            """;

        try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
            ps.setInt(1, 1); // product_id
            ps.setInt(2, 1); // customer_id
            ps.setInt(3, 2); // quantity

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                System.out.printf("✅ Создан заказ ID=%d\n", orderId);
            }
        }
    }

    private static void printLatestOrders(Connection conn) throws SQLException {
        System.out.println("\n🔹 3. Последние 5 заказов:");

        String query = """
            SELECT o.id, c.first_name || ' ' || c.last_name AS customer,
                   p.description AS product, o.quantity, o.order_date, s.name AS status
            FROM "order" o
            JOIN customer c ON o.customer_id = c.id
            JOIN product p ON o.product_id = p.id
            JOIN order_status s ON o.status_id = s.id
            ORDER BY o.order_date DESC
            LIMIT 5;
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("%-5s %-20s %-30s %-10s %-12s %-10s%n",
                    "ID", "Клиент", "Товар", "Кол-во", "Дата", "Статус");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-30s %-10d %-12s %-10s%n",
                        rs.getInt("id"),
                        rs.getString("customer"),
                        rs.getString("product"),
                        rs.getInt("quantity"),
                        rs.getDate("order_date"),
                        rs.getString("status"));
            }
        }
    }

    private static void updateProductPriceAndStock(Connection conn) throws SQLException {
        System.out.println("\n🔹 4. Обновление цены и остатка товара...");

        String updateProduct = """
            UPDATE product
            SET price = price * 1.05, quantity_in_stock = quantity_in_stock - 1
            WHERE id = ?
            RETURNING description, price, quantity_in_stock;
            """;

        try (PreparedStatement ps = conn.prepareStatement(updateProduct)) {
            ps.setInt(1, 1); // обновляем товар с ID=1

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String desc = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("quantity_in_stock");
                System.out.printf("✅ Товар '%s' обновлён: цена=%.2f, остаток=%d\n", desc, price, stock);
            }
        }
    }

    private static void deleteTestRecords(Connection conn) throws SQLException {
        System.out.println("\n🔹 5. Удаление тестовых записей...");

        String deleteOrder = "DELETE FROM \"order\" WHERE customer_id = (SELECT id FROM customer WHERE email = 'ivan.petrov@example.com') RETURNING id;";
        String deleteCustomer = "DELETE FROM customer WHERE email = 'ivan.petrov@example.com' RETURNING id;";

        try (PreparedStatement psOrder = conn.prepareStatement(deleteOrder);
             PreparedStatement psCustomer = conn.prepareStatement(deleteCustomer)) {

            // Удаляем заказы
            ResultSet rsOrder = psOrder.executeQuery();
            int deletedOrders = 0;
            while (rsOrder.next()) deletedOrders++;
            System.out.printf("✅ Удалено %d заказов\n", deletedOrders);

            // Удаляем клиента
            ResultSet rsCust = psCustomer.executeQuery();
            if (rsCust.next()) {
                int deletedCustomerId = rsCust.getInt(1);
                System.out.printf("✅ Удалён клиент ID=%d\n", deletedCustomerId);
            }
        }
    }
}