package homeTast_6_lambda.task_3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Task3 {
    public static void main(String[] args) {
        Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(1,2,3));
        Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(0,1,2,4));

        System.out.println("set1: " + set1);
        System.out.println("set2: " + set2);

        System.out.println("Пересечение: " + PowerfulSet.intersection(set1, set2));
        System.out.println("Объединение: " + PowerfulSet.union(set1, set2));
        System.out.println("Разница: " + PowerfulSet.relativeComplement(set1, set2));

    }
}
