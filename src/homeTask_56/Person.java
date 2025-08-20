package homeTask_56;

import java.util.ArrayList;
import java.util.Scanner;

public class Person {
    private String Name;
    private double money;
    private ArrayList<Product> products; //делаю сразу с дженериками, хоть их нет в программе. лучше сразу стану привыкать

    public Person(String name, double money) {
        this.Name = name;
        this.money = money;
        this.products = new ArrayList<>(); // без дженерика будет возможно вписать любуй объект типо такого: products.add("случайная строка"); // ОШИБКА, но компилятор пропустит
    }

    public String getName() {
        return Name;
    }

    // также переопрелелил метод для класса персона
    @Override
    public String toString() {
        return Name + " (" + money + ") ";
    }
    // создаю метод-процедуру для похода в мазагин.
    public void doShopping(Scanner scanner, ArrayList<Product> products) {
        if (products.isEmpty()) {
            System.out.println("🏪 Магазин пуст. Нечего покупать.");
            return; // На случай если нет списка доступных к покупке товаров
        }
        //
        System.out.println("Доступные товары:");
        for (int i = 0; i < products.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + products.get(i));
        }
        System.out.println("  0. Завершить покупки\n");
        //
        while (true) {
            System.out.print("Выберите номер товара (или 0 для выхода): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Введите число.");
                continue;
            }
            if (choice == 0) {
                System.out.println("✅ Покупки завершены. До свидания, " + Name + "!");
                break;
            } else if (choice < 1 || choice > products.size()) {
                System.out.println("❌ Нет товара с таким номером.");
                continue;
            }
            Product selectedProduct = products.get(choice - 1); //поскольку нумерация массива с 0, а продуктов с 1.
            addProduct(selectedProduct);
            System.out.println(); // для читаемости
        }

    }
    // метод добавления продукта в список покупок
    public void addProduct(Product product) {
        if (this.money < product.getPrice()) {
            System.out.println(Name + " не может позволить себе " + product.getName());
        } else {
            this.products.add(product);         //добавляем товар в сумку пользователя
            this.money -= product.getPrice();   //вычитаем стоимость из кеша пользователя
        }
    }
    // метод для вывода списка продуктов
    public void listProducts() {
        System.out.print(toString());
        System.out.println("и её cписок купленных продуктов: ");
        if (products.isEmpty()) {
            System.out.println("Пока ничего не куплено");
        } else {
            for (Product product : products) {
                System.out.println("- " + product);
            }
        }
    }
}
