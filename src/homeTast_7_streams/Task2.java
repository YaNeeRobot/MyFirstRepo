package homeTast_7_streams;

import java.util.Scanner;

public class Task2 {
    public  static void main(String[] args) {
        //------------------------------------ вводим строки как нам сказано
        Scanner sc = new Scanner(System.in);
        System.out.println("введите строку S");
        String s = sc.nextLine();
        System.out.println("введите строку Т");
        String t = sc.nextLine();
        //------------------------------------ используем функ интерфейс для реализации функции реверса строки
        ReverseInterface reverseInterface = value -> new StringBuilder(value).reverse().toString();
        System.out.println(reverseInterface.getReverseString(t));
        System.out.println(s);
    }
}
