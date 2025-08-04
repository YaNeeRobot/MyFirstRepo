package homeTask3;

import java.util.Random;

public class Televizor {

    // Переменные класса
    private int size_vertical;           // Высота
    private int size_horizontal;         // Ширина
    private String color;                // Цвет
    private String Vendor;               // Бренд
    private boolean state = false;
    Random rand = new Random();
    private boolean defective = rand.nextBoolean();

    // Функции класса
    public String show(){
        return "высота - " + getSize_vertical() + "см, ширина - " + getSize_horizontal()  + "см, цвет - " + getColor() + ", производитель - " + getVendor();
    }
    public boolean on_Off() {
        if (!defective) {
            if (!state) {
                state = true;
                System.out.println("Включился");
            } else {
                state = false;
                System.out.println("Выключился");
            }
            return state;
        } else {
            System.out.println("Не включается! Возможно бракованый");
            return false;
        }
    }

    // Конструктор явный
    public Televizor(int size_vertical, int size_horizontal, String color, String Vendor) {
        this.size_vertical = size_vertical;
        this.size_horizontal = size_horizontal;
        this.color = color;
        this.Vendor = Vendor;
    }

    // Геттеры
    public int getSize_vertical() {
        return size_vertical;
    }

    public int getSize_horizontal() {
        return size_horizontal;
    }

    public String getColor() {
        return color;
    }

    public String getVendor() {
        return Vendor;
    }
}
