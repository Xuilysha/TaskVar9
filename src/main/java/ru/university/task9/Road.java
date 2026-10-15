package ru.university.task9;

public class Road {
    int[][] cells;

    Road(int lanes, int size) {
        if (lanes < 1 || lanes > 4 || size < 1) {
            throw new IllegalArgumentException("неверный размер дороги");
        }
        cells = new int[lanes][size];
    }

    boolean add(Car car) {
        if (car.id < 1 || car.len < 1 || car.lane < 0 || car.lane >= cells.length ||
                car.pos < 0 || car.pos + car.len > cells[0].length) {
            return false;
        }

        for (int i = car.pos; i < car.pos + car.len; i++) {
            if (cells[car.lane][i] != 0) {
                return false;
            }
        }

        for (int i = car.pos; i < car.pos + car.len; i++) {
            cells[car.lane][i] = car.id;
        }
        return true;
    }

    void show() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                System.out.print(cells[i][j] == 0 ? ". " : cells[i][j] + " ");
            }
            System.out.println();
        }
    }
}
