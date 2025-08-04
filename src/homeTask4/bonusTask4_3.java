package homeTask4;

import java.lang.reflect.Array;
import java.util.Arrays;

public class bonusTask4_3 {
    //дано:
    public static String stroka = "COURCE Java";

    //решение:
    public static void main(String[] args) {
        String result = myMagicFunction(stroka);
        System.out.println(result);
    }

    public static String myMagicFunction(String input) {
        //
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();
        //
        for (String word : words) {
            //переводим слово в нижний регистр
            String LowerWord = word.toLowerCase();

            //преобразуем слово в массив символов
            char[] letters = LowerWord.toCharArray();

            //Сортируем символы по возрастанию
            Arrays.sort(letters);

            //Добавляем отсортированое слово к результату и добавляем пробел
            result.append(new String(letters)).append(" ");
        }
        //Выводим результат, удаляя в конце строки пробел (после последнего слова)
        return result.toString().trim();
    }
}
