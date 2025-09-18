package homeTast_6_lambda.task_2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CheckerAnagram {
    // реализация через сортировку символов
    public boolean momsCheckerAnagram(String s, String t) {
        // проверка по длине
        if (s.length() != t.length()) {
            return false;
        }
        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();
        Arrays.sort(sArray);
        Arrays.sort(tArray);
        // сравниваем отсортированые массивы
        return Arrays.equals(sArray,tArray); // возврат результата true если два массива одинаковые
    }

    // реализация через Map-у
    public  boolean checkerAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        Map<Character, Integer> charCount = new HashMap<>();
        // считаем символы в строке s
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }

        // вычитаем символы из строки t
        for (char c : t.toCharArray()) {
            if (!charCount.containsKey(c)){
                return false;
            }
            charCount.put(c, charCount.get(c) - 1);
            if (charCount.get(c) == 0){
                charCount.remove(c);
            }
        }
        return charCount.isEmpty(); // будет true если колличество уникальных символов одинаково для строк s и t
    }
}
