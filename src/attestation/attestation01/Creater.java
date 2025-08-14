package attestation.attestation01;

import java.util.ArrayList;
import java.util.Scanner;

public class Creater {
    // создал сканнер
    private Scanner scanner = new Scanner(System.in);

    //создание персоны
    public Person createPerson(Scanner scanner) {
        String Name;
        while (true) {                  //ввод имени
            System.out.println("Введите имя персоны: ");
            Name = scanner.nextLine().trim();
            if (Name.isEmpty()) {                                       // Проверка на null?
                System.out.println("❌ Имя не может быть пустым. Попробуйте снова.");
            } else if (Name.length() < 3) {                             // Проверка на 3 символа
                System.out.println("❌ Имя должно быть не менее 3 символов. Попробуйте снова.");
            } else {
                break;
            }
        }

        double money = 0;
        while (true) {                  //ввод денег
            System.out.println("Введите сумму денег: ");
            try {
                money = Double.parseDouble(scanner.nextLine());
                if (money < 0) {
                    System.out.println("Сумма не может быть отрицательной. Попробуйте снова.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите число.");
            }
        }
        System.out.println("✅ Персона создана: " + Name + " (" + money + " руб)\n");
        return new Person(Name, money);
    }

    //создание продукта
    public Product createProduct(Scanner scanner) {
        String name;
        while (true) {
            System.out.println("Введите наименование продукта: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("❌ Наименование не может быть пустым. Попробуйте снова.");
            } else if (name.length() < 2) {                             // Проверка на 2 символа
                System.out.println("❌ Наименование должно быть не менее 2 символов. Попробуйте снова.");
            } else {
                break;
            }
        }
        double price;
        while (true) {
            System.out.println("Введите сумму денег: ");
            try {
                price = Double.parseDouble(scanner.nextLine());
                if (price < 0) {
                    System.out.println("Сумма не может быть отрицательной. Попробуйте снова.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите число.");
            }
        }
        System.out.println("✅ Продукт создан: " + name + " (" + price + " руб)\n");
        return new Product(name, price);
    }

    // Метод создания списка продуктов
    public ArrayList<Product> createProductList(Scanner scanner) {
        ArrayList<Product> products = new ArrayList<>();
        int number = 1;

        while (true) {
            System.out.println("Товар #" + number + ":");
            Product product = createProduct(scanner);
            products.add(product);

            System.out.print("Добавить ещё один товар? (да/нет): ");
            String more = scanner.nextLine().trim().toLowerCase();
            if (more.equals("да") || more.equals("yes") || more.equals("y")) {
            } else {
                break;
            }
            number++;
        }
        return products;
    }
}
