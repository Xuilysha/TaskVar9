package ru.university.task9.model;

public class Scenario {
    public int id;
    public String name;
    public String kind;
    public int roads;
    public int lanes;
    public int size;
    public int stopRoad;

    public Scenario(int id, String name, String kind, int roads, int lanes, int size, int stopRoad) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.roads = roads;
        this.lanes = lanes;
        this.size = size;
        this.stopRoad = stopRoad;
    }
}
