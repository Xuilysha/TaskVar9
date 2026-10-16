package ru.university.task9;

public class Main {
    public static void main(String[] args) {
        Road road = new Road(2, 8);
        Car a = new Car(1, "грузовик", 2, 0, 1);
        Car b = new Car(2, "легковая", 1, 0, 0);
        Car c = new Car(3, "автопоезд", 3, 1, 2);

        road.add(b);
        road.add(a);
        road.add(c);
        System.out.println("начало");
        road.show();

        for (int i = 1; i <= 4; i++) {
            System.out.println("шаг " + i);
            road.step();
            road.show();
        }
    }
}
