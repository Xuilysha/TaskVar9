package ru.university.task9;

public class Crossroad {
    Road[] in;
    Road[] out;
    Road center;
    boolean[] stop;
    int green = 0;
    int time = 0;

    Crossroad(int count, int lanes, int size) {
        if (count < 3 || count > 4) {
            throw new IllegalArgumentException("нужно 3 или 4 дороги");
        }
        in = new Road[count];
        out = new Road[count];
        stop = new boolean[count];
        center = new Road(1, size);
        for (int i = 0; i < count; i++) {
            in[i] = new Road(lanes, size);
            out[i] = new Road(lanes, size);
        }
    }

    boolean add(Car car, int from, int to) {
        if (from < 0 || from >= in.length || to < 0 || to >= out.length) {
            return false;
        }
        if (!in[from].add(car)) {
            return false;
        }
        car.to = to;
        return true;
    }

    void step() {
        for (Road road : out) {
            road.step();
        }

        if (!center.cars.isEmpty()) {
            Car car = center.cars.get(0);
            if (car.pos + car.len == center.cells[0].length && out[car.to].canAdd(car, 0, 0)) {
                center.remove(car);
                car.lane = 0;
                car.pos = 0;
                out[car.to].add(car);
            } else {
                center.step();
            }
        }

        if (center.cars.isEmpty()) {
            for (int i = 0; i < in[green].cars.size(); i++) {
                Car car = in[green].cars.get(i);
                if (car.pos + car.len != in[green].cells[car.lane].length) {
                    continue;
                }
                if (stop[green] && !car.stopped) {
                    car.stopped = true;
                    break;
                }
                in[green].remove(car);
                car.lane = 0;
                car.pos = 0;
                center.add(car);
                break;
            }
        }

        for (Road road : in) {
            road.step();
        }
        time++;
        if (time == 5) {
            time = 0;
            green = (green + 1) % in.length;
        }
    }

    void show() {
        System.out.println("зеленый для дороги " + (green + 1));
        for (int i = 0; i < in.length; i++) {
            System.out.println("въезд " + (i + 1));
            in[i].show();
        }
        System.out.println("центр");
        center.show();
        for (int i = 0; i < out.length; i++) {
            System.out.println("выезд " + (i + 1));
            out[i].show();
        }
    }
}
