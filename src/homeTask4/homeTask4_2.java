package homeTask4;

public class homeTask4_2 {
    //Начальные условия
    static String initialRow = "<>-->>><---<<>>----<<<---->><<-->---><<----<>>>";
    static int countArrows = 0;
    // Варианты стрел
    static String varA = ">>-->";
    static String varB = "<--<<";
    static String varC = ">>--->";
    static String varD = "<---<<";
    // main
    public static void main(String[] args) {
        //Выполняем
        countArrows = counter(initialRow);
        System.out.println("Кол-во стрел = " + countArrows);
    }

    public static int counter(String initialRow) {
        int count = 0;
        int countAB = 0;
        int countCD = 0;
        // Проходим по последовательности, проверя каждую возможную подстроку длиной 5, (для вариантов А и В)
        for (int i = 0; i < initialRow.length() - 5; i++) {
            String subString = initialRow.substring(i, i + 5);
            if (subString.equals(varA) || subString.equals(varB)){
                countAB++;
            }
        }
        System.out.println("коротких стрел = "+countAB);
        for (int i = 0; i < initialRow.length() - 6; i++) {
            String subString = initialRow.substring(i, i + 6);
            if (subString.equals(varC) || subString.equals(varD)){
                countCD++;
            }
        }
        System.out.println("длинных стрел = "+countCD);
        count = countAB + countCD;
        return count;
    }
}