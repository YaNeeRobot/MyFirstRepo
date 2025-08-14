package attestation.attestation01;

public class Product {
    private String name;
    private double price;
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    //переопределение метода toString для класса продукт, выводим его атрибуты.
    @Override
    public String toString() {
        return name + " (" + price + " руб)";
    }
    //стоимость продукта
    public double getPrice() {
        return price;
    }
    //наименование продукта
    public String getName() {
        return name;
    }
}
