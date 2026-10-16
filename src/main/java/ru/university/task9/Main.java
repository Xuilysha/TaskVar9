package ru.university.task9;

public class Main {
    public static void main(String[] args) {
        Crossroad cross = new Crossroad(4, 1, 5);
        Car a = new Car(1, "легковая", 1, 0, 4);
        Car b = new Car(2, "грузовик", 2, 0, 3);
        Car c = new Car(3, "легковая", 1, 0, 4);

        cross.add(a, 0, 2);
        cross.add(b, 1, 3);
        cross.add(c, 2, 0);
        cross.stop[1] = true;

        for (int i = 1; i <= 16; i++) {
            System.out.println("шаг " + i);
            cross.step();
            cross.show();
        }
    }
}
