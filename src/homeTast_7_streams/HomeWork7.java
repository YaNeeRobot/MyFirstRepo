package homeTast_7_streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HomeWork7 {
    public static void main(String[] args) {
        // Входные данные
        String[] inputData = {
                "a123me|Mercedes|White|0|8300000",
                "b873of|Вольга|Black|0|673000",
                "w487mn|Lexus|Grey|76000|900000",
                "p987hj|Вольга|Red|610|704340",
                "c987ss|Toyota|White|254000|761000",
                "o983op|Toyota|Black|698000|740000",
                "p146op|BMW|White|271000|850000",
                "u893ii|Toyota|Purple|210900|440000",
                "l097df|Toyota|Black|108000|780000",
                "y876wd|Toyota|Black|160000|1000000"
        };
        //Создание коллекции с помощью стрима. Выбрал эрейЛист потому что элементы не уникальные
        List<Car> cars = Stream.of(inputData)
                .map(Car::fromString)   //преобразуем каждую строку в объект Сar
                .toList();              //собираем в список
        // Проверим результат
        System.out.println("=== Созданные автомобили ===");
        cars.forEach(System.out::println);

        //Выполняем 4 пункта

        // 1) Номера всех автомобилей, имеющих заданный в переменной цвет
        //colorToFind или нулевой пробег mileageToFind.
        String colorToFind = "Black";
        int mileageToFind = 0;

        System.out.println("\n=== 1. Номера автомобилей с цветом '" + colorToFind + "' или пробегом " + mileageToFind + " ===");
        cars.stream()
                //фильтр  эл. кол-ции           сравнение строк       или       стравнение интежеров
                .filter(car -> car.getColor().equals(colorToFind) || car.getMileage() == mileageToFind)
                // выводим номера которые прошли фильтр
                .map(Car::getNumber)
                // делаем вывод строк
                .forEach(System.out::println);

        // 2) Количество уникальных моделей в ценовом диапазоне от n до m
        double n = 500_000;
        double m = 900_000;
        System.out.println("\n=== 2. Уникальные модели в диапазоне от " + n + " до " + m + " руб. ===");

        long uniqueModelCount = cars.stream()
                .filter(car -> car.getPrice() >= n && car.getPrice() <= m)
                .map(Car::getModel) // для каждого car вызывает getModel
                .distinct()         // убирает дублекаты, оставляет уникальные модели
                .count();           // Подсчитывает количество элементов в потоке — возвращает long
        System.out.println("Количество уникальных моделей: " + uniqueModelCount);

        // 3) Вывести цвет автомобиля с минимальной стоимостью.
        System.out.println("\n=== 3. Цвет автомобиля с минимальной стоимостью ===");
        cars.stream()
                .min(Comparator.comparingInt(Car::getPrice))
                .map(Car::getColor)
                .ifPresentOrElse(
                        color -> System.out.println("Цвет самого дешевого авто: " + color),
                        () -> System.out.println("Cписок пуст")
                );

        // 4) Среднюю стоимость искомой модели modelToFind
        String modelToFind = "Вольга";
        System.out.println("\n=== 4. Средняя стоимость модели '" + modelToFind + "' ===");
        cars.stream()
                .filter(car -> car.getModel().equals(modelToFind))
                .mapToDouble(Car::getPrice)
                .average()
                .ifPresentOrElse(
                        avg -> System.out.printf("Средняя стоимость: %.2f руб.%n", avg),
                        () -> System.out.println("Модель не найдена")
                );
    }
}
