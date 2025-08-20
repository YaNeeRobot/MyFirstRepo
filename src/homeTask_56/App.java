package homeTask_56;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Creater creater = new Creater();
        Scanner scanner = new Scanner(System.in);

        // Создание товаров
        System.out.println("🛒 СОЗДАНИЕ ТОВАРОВ В МАГАЗИНЕ");
        ArrayList<Product> products = creater.createProductList(scanner);

        // Создание нескольких покупателей
        System.out.println("\n🧍 СОЗДАНИЕ ПОКУПАТЕЛЕЙ");
        ArrayList<Person> persons = creater.createPersonList(scanner);

        // Покупки. В моей программе вместо слова END я использую "0" для завершения покупки.
        System.out.println("\n🛍️ НАЧИНАЕТСЯ ВЕЛИКИЙ ШОПИНГ!");
        for (Person person : persons) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println(person.getName() + ", добро пожаловать в магазин!");
            person.doShopping(scanner, products);
        }

        // Итог
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📋 ИТОГИ ПОКУПОК:");
        for (Person person : persons) {
            person.listProducts();
        }

        scanner.close();
    }
}
