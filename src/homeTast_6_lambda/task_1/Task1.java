package homeTast_6_lambda.task_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set; //Импортировали чтобы использовать Set

public class Task1 {
    static <T> Set<T> myMethod(ArrayList<T> list){
        if(list == null) { // null - значит не инициализирован, .isEmpty - если список просто пустой.
            throw new NullPointerException("список не инициализирован!");
        }
        return new LinkedHashSet<>(list); // делаю связанный список, чтобы порядок сохранить.
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList("s","f","a","a","c")); // создаю (5 штук!!) тестовый массив элементов
        Set<String> setOfUniques = myMethod(list); // передаем моему методу 5 шт, метод возвращает Set
        System.out.println(setOfUniques); // в выводе получаем только уникальные элементы (4 штуки!)
    }
}
