package homeTast_6_lambda.task_2;

import java.util.Scanner;

public class Task2 {
    public  static void main(String[] args) {
        CheckerAnagram checker = new CheckerAnagram();
        //------------------------------------ вводим строки как нам сказано
        Scanner sc = new Scanner(System.in);
        System.out.println("введите строку S");
        String s = sc.nextLine();
        System.out.println("введите строку Т");
        String t = sc.nextLine();
        //------------------------------------ запускаем метод
        System.out.println("строки являются анаграммами?");
        System.out.println("метод №1: " + checker.checkerAnagram(s,t));
        System.out.println("метод №2: " + checker.momsCheckerAnagram(s,t));
        //------------------------------------ закрываем sc
        sc.close();
    }
}
