package ru.university.task9;

import java.util.ArrayList;

public class Roundabout {
    Road[] in;
    Road[] out;
    int[][] ring;
    ArrayList<Car> cars = new ArrayList<>();
    int part;

    Roundabout(int count, int lanes, int size) {
        if (count < 3 || count > 6 || lanes < 1 || lanes > 4 || size < 1) {
            throw new IllegalArgumentException("неверный размер кольца");
        }
        in = new Road[count];
        out = new Road[count];
        part = Math.max(4, size);
        ring = new int[lanes][count * part];
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

    boolean changeLane(Car car, int from, int lane) {
        if (from < 0 || from >= in.length) {
            return false;
        }
        return in[from].changeLane(car, lane);
    }

    boolean changeLane(Car car, int lane) {
        if (!cars.contains(car) || Math.abs(car.lane - lane) != 1 ||
                lane < 0 || lane >= ring.length) {
            return false;
        }
        int n = ring[lane].length;
        for (int i = 0; i < car.len; i++) {
            if (ring[lane][(car.pos + i) % n] != 0) {
                return false;
            }
        }
        for (int i = 0; i < car.len; i++) {
            ring[car.lane][(car.pos + i) % n] = 0;
            ring[lane][(car.pos + i) % n] = car.id;
        }
        car.lane = lane;
        return true;
    }

    void step() {
        for (Road road : out) {
            road.step();
        }

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            int n = ring[car.lane].length;
            if (car.ringPassed == car.ringTarget) {
                if (out[car.to].canAdd(car, car.lane, 0)) {
                    for (int j = 0; j < car.len; j++) {
                        ring[car.lane][(car.pos + j) % n] = 0;
                    }
                    cars.remove(i);
                    car.pos = 0;
                    out[car.to].add(car);
                    i--;
                }
                continue;
            }

            int next = (car.pos + car.len) % n;
            if (ring[car.lane][next] == 0) {
                ring[car.lane][car.pos] = 0;
                ring[car.lane][next] = car.id;
                car.pos = (car.pos + 1) % n;
                car.ringPassed++;
            }
        }

        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[i].cars.size(); j++) {
                Car car = in[i].cars.get(j);
                if (car.pos + car.len != in[i].cells[car.lane].length) {
                    continue;
                }
                int start = i * part;
                boolean free = true;
                for (int k = 0; k < car.len; k++) {
                    if (ring[car.lane][start + k] != 0) {
                        free = false;
                    }
                }
                if (!free) {
                    continue;
                }
                in[i].remove(car);
                car.pos = start;
                car.ringPassed = 0;
                int exit = (car.to * part - 1 + ring[car.lane].length) % ring[car.lane].length;
                car.ringTarget = (exit - (start + car.len - 1) + ring[car.lane].length) % ring[car.lane].length;
                for (int k = 0; k < car.len; k++) {
                    ring[car.lane][start + k] = car.id;
                }
                cars.add(car);
                j--;
            }
        }

        for (Road road : in) {
            road.step();
        }
    }

    void show() {
        for (int i = 0; i < in.length; i++) {
            System.out.println("въезд " + (i + 1));
            in[i].show();
        }
        System.out.println("кольцо");
        for (int i = 0; i < ring.length; i++) {
            for (int j = 0; j < ring[i].length; j++) {
                System.out.print(ring[i][j] == 0 ? ". " : ring[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < out.length; i++) {
            System.out.println("выезд " + (i + 1));
            out[i].show();
        }
    }
}
