package ru.university.task9;

public class Car {
    int id;
    String tip;
    int len;
    int lane;
    int pos;
    int to;
    String turn;
    boolean stopped;
    int ringPassed;
    int ringTarget;

    public Car(int id, String tip, int len, int lane, int pos) {
        this.id = id;
        this.tip = tip;
        this.len = len;
        this.lane = lane;
        this.pos = pos;
    }
}
