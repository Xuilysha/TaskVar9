package ru.university.task9;

public class Main {
    public static void main(String[] args) {
        Road road = new Road(2, 10);
        Car a = new Car(1, "легковая", 1, 0, 1);
        Car b = new Car(2, "грузовик", 2, 1, 3);

        System.out.println(a.tip + ": " + road.add(a));
        System.out.println(b.tip + ": " + road.add(b));
        road.show();
    }
}
