package homeTask_56;

public class DiscountProduct extends Product {
    private double discount;
    private double discountedPrice;

    public DiscountProduct(String name, double originalPrice, short discount) {
        super(name, originalPrice);
        // валидация для переменной скидка
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("не подходит такое число скидки, нужно от 0 до 100");
        }
        this.discount = discount;
        this.discountedPrice = originalPrice * (1 - discount / 100);  // Пересчёт ценника
    }

    @Override
    public double getPrice() {
        return discountedPrice;
    }

    @Override
    public String toString() {
        return getName() + " (" + discountedPrice + " руб)";
    }
}