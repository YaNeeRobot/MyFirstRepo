package attestation.attestation01;

import java.util.ArrayList;

public class Person {
    private String Name;
    private double money;
    private ArrayList<Product> products; //делаю сразу с дженериками, хоть их нет в программе. лучше сразу стану привыкать

    public Person(String name, double money) {
        this.Name = name;
        this.money = money;
        this.products = new ArrayList<>(); // без дженерика будет возможно вписать любуй объект типо такого: products.add("случайная строка"); // ОШИБКА, но компилятор пропустит
    }

    // также переопрелелил метод для класса персона
    @Override
    public String toString() {
        return Name + " (" + money + ") ";
    }
    // создаю метод-процедуру для похода в мазагин.
    public void doShopping() {
        //
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
        System.out.println("и ( ✅ ) её cписок купленных продуктов: ");
        if (products.isEmpty()) {
            System.out.println("Пока ничего не куплено");
        } else {
            for (Product product : products) {
                System.out.println("- " + product);
            }
        }
    }
}
