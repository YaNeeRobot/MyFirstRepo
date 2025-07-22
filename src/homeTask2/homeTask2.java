import java.util.Scanner;

public class homeTask2 {
    public static void main(String[] args) {
        System.out.println("Представьтесь, пожалуйста (в консоль пиши):");
        Scanner input = new Scanner(System.in);
        String username = input.nextLine();  // считывание происходит один раз, и после строка удаляется из потока
        System.out.println("Здравствуй, " + username + "!");
        System.out.println(input.nextLine());
        System.out.println(username + " ебен бобен ;D");
    }
}
