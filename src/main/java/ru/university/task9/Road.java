package ru.university.task9;

import java.util.ArrayList;

public class Road {
    int[][] cells;
    ArrayList<Car> cars = new ArrayList<>();

    Road(int lanes, int size) {
        if (lanes < 1 || lanes > 4 || size < 1) {
            throw new IllegalArgumentException("неверный размер дороги");
        }
        cells = new int[lanes][size];
    }

    boolean add(Car car) {
        if (!canAdd(car, car.lane, car.pos)) {
            return false;
        }

        for (int i = car.pos; i < car.pos + car.len; i++) {
            cells[car.lane][i] = car.id;
        }
        cars.add(car);
        return true;
    }

    boolean canAdd(Car car, int lane, int pos) {
        if (car.id < 1 || car.len < 1 || lane < 0 || lane >= cells.length ||
                pos < 0 || pos + car.len > cells[0].length) {
            return false;
        }

        for (Car a : cars) {
            if (a.id == car.id) {
                return false;
            }
        }

        for (int i = pos; i < pos + car.len; i++) {
            if (cells[lane][i] != 0) {
                return false;
            }
        }
        return true;
    }

    void remove(Car car) {
        if (!cars.remove(car)) {
            return;
        }
        for (int i = car.pos; i < car.pos + car.len; i++) {
            cells[car.lane][i] = 0;
        }
    }

    boolean changeLane(Car car, int lane) {
        if (!cars.contains(car) || Math.abs(car.lane - lane) != 1 ||
                lane < 0 || lane >= cells.length) {
            return false;
        }
        for (int i = car.pos; i < car.pos + car.len; i++) {
            if (cells[lane][i] != 0) {
                return false;
            }
        }
        for (int i = car.pos; i < car.pos + car.len; i++) {
            cells[car.lane][i] = 0;
            cells[lane][i] = car.id;
        }
        car.lane = lane;
        return true;
    }

    void step() {
        for (Car car : cars) {
            int next = car.pos + car.len;
            if (next >= cells[car.lane].length || cells[car.lane][next] != 0) {
                continue;
            }
            cells[car.lane][car.pos] = 0;
            cells[car.lane][next] = car.id;
            car.pos++;
        }
    }

    void show() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                System.out.print(cells[i][j] == 0 ? ". " : cells[i][j] + " ");
            }
            System.out.println();
        }
    }

    public String view() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                s.append(cells[i][j] == 0 ? ". " : cells[i][j] + " ");
            }
            s.append("\n");
        }
        return s.toString();
    }
}
