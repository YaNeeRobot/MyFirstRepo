package homeTask4;

import java.util.Scanner;

public class homeTask4_1 {
    public static void main(String[] args) {
        // Создам строковое представление своей кверти (чисто алфавитные)
        String topRow = "qwertyuiop";
        String middleRow = "asdfghjkl";
        String bottomRow = "zxcvbnm";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите символ латинский: ");
        //здесь сканер считывает первое слово - переводит в нижный регистр - берет начальный символ слова
        // поскольку мои ряды в нижнем регистре..
        char inputChar = scanner.next().toLowerCase().charAt(0);

        // Проверка символа
        if (!Character.isLetter(inputChar) || inputChar < 'a' || inputChar > 'z') {
            System.out.println("Ошибка: введен недопустимый символ.");
            return;
        }

        char charNeighbor = findCharNeighbor(inputChar,topRow,middleRow,bottomRow);
        System.out.println("Слева от '" + inputChar + "' находится буква '" + charNeighbor + "'");
    }
    private static char findCharNeighbor(char c, String topRow, String middleRow, String bottomRow) {
        //ищем символ в верхнем ряду
        int index = topRow.indexOf(c);
        if(index != -1){
            if(index == 0){
                return topRow.charAt(topRow.length()-1);
            } else {
                return topRow.charAt(index - 1);
            }
        }
        //Тоже самое делаем для среднего
        index = middleRow.indexOf(c);
        if(index != -1){
            if(index == 0){
                return middleRow.charAt(middleRow.length()-1);
            } else {
                return middleRow.charAt(index - 1);
            }
        }
        //И также для нижнего ряда
        index = bottomRow.indexOf(c);
        if(index != -1){
            if(index == 0){
                return bottomRow.charAt(bottomRow.length()-1);
            } else {
                return bottomRow.charAt(index - 1);
            }
        }
        System.out.println("этот символ не подходящий. Ваш символ: '" + c + "'");
        return c; // на случай если не нашли символ в рядах. ОБНОВЛЕНИЕ!! -> Сделаем проверку
    }
}
