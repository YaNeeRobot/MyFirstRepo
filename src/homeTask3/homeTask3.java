package homeTask3;

import java.util.Scanner;

public class homeTask3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nПриветствую! Опиши какая высота и ширина (ты только интеджер используй!)");
        int height = Integer.parseInt(scanner.nextLine());
        int width = Integer.parseInt(scanner.nextLine());
        System.out.println("Какой цвет");
        String color = scanner.nextLine();

        Televizor tv1 = new Televizor(height,width,color,"noname");
        System.out.println("Ок. вот вариант: " + tv1.show());
        System.out.println("Попробуем включить");
        tv1.on_Off();
    }
}
