import java.util.function.*;

public class Main {

    public static void main(String[] args) {
        // Функциональный интерфейс Consumer<T>

        // Задача. Реализовать действие, которое реверсирует целое число і
        // выводит результат на экран.
        // 1. Объявить ссылку на интерфейс Consumer<T> для типа Integer
        Consumer<Integer> cn;

        // 2. Реализовать лямбда-выражение
        cn = (n) -> {
            Integer t = n, number = 0;
            while (t>0) {
                number = number * 10 + t % 10;
                t = t/10;
            }
            System.out.println("number = " + number);
        };

        // 3. Вызвать метод accept()
        cn.accept(219);
    }
}