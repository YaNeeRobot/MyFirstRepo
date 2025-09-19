package homeTast_6_lambda.task_3;

import java.util.HashSet;
import java.util.Set;

public class PowerfulSet {
    //intersection - пересечение двух наборов
    public static  <T> Set<T> intersection(Set<T> set1, Set<T> set2){
        if (set1 == null || set2 == null){
            return new HashSet<T>();
        }
        Set<T> result = new HashSet<>(set1); // переписал set1 в результирующий набор
        result.retainAll(set2); // из первого набора вычитаем то, чего нет во втором наборе
        return result;
    }

    // union - объединение двух наборов
    public static <T> Set<T> union(Set<T> set1, Set<T> set2){
        if (set1 == null || set2 == null){
            return new HashSet<>(); // раз они оба пустые, возвращаем пустой хешсет
        }
        if (set1 == null){
            return new HashSet<>(set2); // если первый пустой, выведем хотябы второй набор, который не пуст.
        }
        if (set2 == null){
            return new HashSet<>(set1); // аналогично симметрично для второго
        }
        Set<T> result = new HashSet<>(set1); // переписал set1 в результирующий набор
        result.addAll(set2); // объединяем два набора
        return result;
    }

    // relativeComplement - "относительное дополнение" возвращает из set1 то чего нет в set2
    public static <T> Set<T> relativeComplement(Set<T> set1, Set<T> set2){
        if (set2 == null){
            return new HashSet<>(set1);
        }
        if (set1 == null){
            return new HashSet<>();
        }
        Set<T> result = new HashSet<>(set1);
        result.removeAll(set2); // удаляем из set1(result) то что есть в set2
        return result;
    }
}
