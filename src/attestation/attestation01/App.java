package attestation.attestation01;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Creater creater = new Creater();
        Scanner scanner = new Scanner(System.in);

        System.out.println("🛒 СОЗДАНИЕ ТОВАРОВ В МАГАЗИНЕ");
        ArrayList<Product> products = creater.createProductList(scanner);
    }
}
