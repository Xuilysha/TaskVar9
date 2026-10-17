package ru.university.task9;

public class Main {
    public static void main(String[] args) {
        Crossroad cross = new Crossroad(4, 2, 5);
        Car a = new Car(1, "легковая", 1, 0, 4);
        Car b = new Car(2, "грузовик", 2, 0, 3);
        Car c = new Car(3, "автопоезд", 3, 1, 2);
        Car d = new Car(4, "легковая", 1, 0, 4);

        cross.add(a, 0, "прямо");
        cross.add(b, 1, "направо");
        cross.add(c, 2, "налево");
        cross.add(d, 3, "разворот");
        cross.stop[1] = true;

        for (int i = 1; i <= 24; i++) {
            if (i == 2) {
                System.out.println("перестроение: " + cross.changeLane(c, 2, 0));
            }
            cross.step();
            System.out.println("шаг " + i + ", зеленый " + (cross.green + 1) +
                    ", в центре " + (cross.center.cars.isEmpty() ? "нет" : cross.center.cars.get(0).id));
        }
        cross.show();

        System.out.println("круговой перекресток");
        Roundabout round = new Roundabout(4, 2, 5);
        Car e = new Car(5, "легковая", 1, 0, 4);
        round.add(e, 0, 2);
        round.add(new Car(6, "грузовик", 2, 1, 3), 1, 3);
        round.add(new Car(7, "автопоезд", 3, 0, 2), 2, 0);
        round.add(new Car(8, "легковая", 1, 1, 4), 3, 1);
        for (int i = 1; i <= 25; i++) {
            round.step();
            if (i == 2) {
                System.out.println("перестроение на кольце: " + round.changeLane(e, 1));
            }
            System.out.println("шаг " + i + ", на кольце " + round.cars.size());
            if (i == 5) {
                round.show();
            }
        }
        round.show();
    }
}
