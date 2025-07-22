package homeTask2;

import java.util.Random;

public class bonusTask2 {
    public static void main(String[] args) {
        Random rand = new Random();
        int Vasily = rand.nextInt(3);
        int Petya = rand.nextInt(3);
        if (Vasily == Petya) {
            System.out.println("Ничья");
        } else if (
                (Vasily == 0 && Petya == 1) || // Камень > Ножницы
                        (Vasily == 1 && Petya == 2) || // Ножницы > Бумага
                        (Vasily == 2 && Petya == 0)    // Бумага > Камень
        ) {
            System.out.println("Василий выиграл");
        } else {
            System.out.println("Пётр выиграл");
        }
    }
}
//коментарий для комита