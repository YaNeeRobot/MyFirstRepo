package homeTask_56;

import java.util.ArrayList;
import java.util.Scanner;

public class Creater {
    // создал сканнер (не нужен если передавать его как параметр)
    //private Scanner scanner = new Scanner(System.in);

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

        System.out.println("Выполнить spawnProduct() ?"); // уменьшим рутину
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("yes") || choice.equals("y") || choice.equals("да")) {
            products = spawnProduct();
            return products;
        }

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
    // Сделаю метод создания листа персон
    public ArrayList<Person> createPersonList(Scanner scanner) {
        ArrayList<Person> persons = new ArrayList<>();
        int number = 1;

        System.out.println("Выполнить spawnPerson() ?");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("yes") || choice.equals("y") || choice.equals("да")) {
            persons = spawnPerson();
            return persons;
        }

        while (true) {
            System.out.println("Покупатель #" + number + ":");
            Person person = createPerson(scanner);
            persons.add(person);

            System.out.print("Добавить ещё одного покупателя? (да/нет): ");
            String more = scanner.nextLine().trim().toLowerCase();
            if (more.equals("yes") || more.equals("y") || more.equals("да")) {
            } else {
                break;
            }
            number++;
        }
        return persons;
    }

    public ArrayList<Person> spawnPerson() {
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(new Person("Павел Андреевич", 10000));
        persons.add(new Person("Анна Петровна", 2000));
        persons.add(new Person("Борис", 10));
        return persons;
    }
    public ArrayList<Product> spawnProduct() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("хлеб", 40));
        products.add(new Product("молоко", 60));
        products.add(new Product("торт", 1000));
        products.add(new Product("кофе", 899));
        products.add(new Product("масло", 150));
        return products;
    }
}
