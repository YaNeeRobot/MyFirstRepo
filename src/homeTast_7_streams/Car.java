package homeTast_7_streams;

public class Car {
    private String number;
    private  String model;
    private String color;
    private int mileage;
    private int price;

    public  Car(String number, String model, String color, int mileage, int price) {
        this.number = number;
        this.model = model;
        this.color = color;
        this.mileage = mileage;
        this.price = price;
    }
    //----------------------
    //Добавлю статический метод для парсинга входных данных из массива
    public static Car fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) {
            throw new IllegalArgumentException("неверный формат строки" + line);
        }
        return  new Car(
                parts[0],                       //номер авто
                parts[1],                       //модель
                parts[2],                       //цвет кузова
                Integer.parseInt(parts[3]),     //пробег
                Integer.parseInt(parts[4])      //цена
        );
    }
    //----------------------
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public int getMileage() {
        return mileage;
    }
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    // переопределим тоСтринг
    @Override
    public String toString() {
        return String.format(
                "Car{number='%s', model='%s', color='%s', mileage=%d km, price=%d руб.}",
                number, model, color, mileage, price
        );
    }
}
