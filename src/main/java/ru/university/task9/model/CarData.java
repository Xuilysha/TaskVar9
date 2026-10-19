package ru.university.task9.model;

public class CarData {
    public int id;
    public int scenarioId;
    public String tip;
    public int len;
    public int lane;
    public int pos;
    public int fromRoad;
    public int toRoad;
    public String turn;
    public int changeStep;
    public int changeLane;

    public CarData(int id, int scenarioId, String tip, int len, int lane, int pos,
                   int fromRoad, int toRoad, String turn, int changeStep, int changeLane) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.tip = tip;
        this.len = len;
        this.lane = lane;
        this.pos = pos;
        this.fromRoad = fromRoad;
        this.toRoad = toRoad;
        this.turn = turn;
        this.changeStep = changeStep;
        this.changeLane = changeLane;
    }
}
