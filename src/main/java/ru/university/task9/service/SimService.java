package ru.university.task9.service;

import java.sql.SQLException;
import java.util.ArrayList;
import ru.university.task9.Car;
import ru.university.task9.Crossroad;
import ru.university.task9.Roundabout;
import ru.university.task9.model.CarData;
import ru.university.task9.model.Scenario;
import ru.university.task9.repository.ScenarioRepository;

public class SimService {
    private ScenarioRepository repo;

    public SimService(ScenarioRepository repo) {
        this.repo = repo;
    }

    public ArrayList<Scenario> all() throws SQLException {
        return repo.all();
    }

    public Scenario get(int id) throws SQLException {
        return repo.get(id);
    }

    public ArrayList<CarData> cars(int id) throws SQLException {
        return repo.cars(id);
    }

    public int addScenario(String name, String kind, int roads, int lanes, int size, int stopRoad) throws SQLException {
        if (name == null || name.trim().isEmpty() || name.length() > 100 ||
                !("cross".equals(kind) || "round".equals(kind)) ||
                roads < 3 || roads > ("cross".equals(kind) ? 4 : 6) ||
                lanes < 1 || lanes > 4 || size < 3 || size > 30 ||
                stopRoad < -1 || stopRoad >= roads || ("round".equals(kind) && stopRoad != -1)) {
            throw new IllegalArgumentException("проверьте название, вид и размер перекрестка");
        }
        return repo.addScenario(new Scenario(0, name.trim(), kind, roads, lanes, size, stopRoad));
    }

    public void deleteScenario(int id) throws SQLException {
        repo.delete(id);
    }

    public int addCar(CarData a) throws SQLException {
        Scenario s = repo.get(a.scenarioId);
        if (s != null && s.kind.equals("cross")) {
            a.toRoad = destination(a.fromRoad, a.turn);
        } else if (s != null) {
            a.turn = "";
        }
        if (s == null || a.tip == null || a.tip.trim().isEmpty() || a.tip.length() > 50 ||
                a.len < 1 || a.len > s.size || a.lane < 0 || a.lane >= s.lanes ||
                a.pos < 0 || a.pos + a.len > s.size || a.fromRoad < 0 || a.fromRoad >= s.roads ||
                a.toRoad < 0 || a.toRoad >= s.roads || a.changeStep < 0 || a.changeStep > 100 ||
                (a.changeStep > 0 && (a.changeLane < 0 || a.changeLane >= s.lanes ||
                        Math.abs(a.changeLane - a.lane) != 1))) {
            throw new IllegalArgumentException("проверьте данные машины");
        }
        for (CarData b : repo.cars(a.scenarioId)) {
            if (b.fromRoad == a.fromRoad && b.lane == a.lane &&
                    a.pos < b.pos + b.len && b.pos < a.pos + a.len) {
                throw new IllegalArgumentException("место на дороге уже занято");
            }
        }
        a.tip = a.tip.trim();
        return repo.addCar(a);
    }

    private int destination(int from, String turn) {
        if ("прямо".equals(turn)) return (from + 2) % 4;
        if ("налево".equals(turn)) return (from + 1) % 4;
        if ("направо".equals(turn)) return (from + 3) % 4;
        if ("разворот".equals(turn)) return from;
        return -1;
    }

    public void deleteCar(int id, int scenarioId) throws SQLException {
        repo.deleteCar(id, scenarioId);
    }

    public String run(int id, int steps) throws SQLException {
        Scenario s = repo.get(id);
        if (s == null) {
            throw new IllegalArgumentException("сценарий не найден");
        }
        if (steps < 0 || steps > 100) {
            throw new IllegalArgumentException("шагов должно быть от 0 до 100");
        }
        ArrayList<CarData> list = repo.cars(id);
        StringBuilder result = new StringBuilder();
        if (s.kind.equals("cross")) {
            Crossroad cross = new Crossroad(s.roads, s.lanes, s.size);
            cross.setStop(s.stopRoad);
            ArrayList<Car> objects = new ArrayList<>();
            for (CarData a : list) {
                Car car = new Car(a.id, a.tip, a.len, a.lane, a.pos);
                objects.add(car);
                if (!cross.add(car, a.fromRoad, a.turn)) {
                    result.append("машина ").append(a.id).append(" не добавлена\n");
                }
            }
            for (int i = 1; i <= steps; i++) {
                for (int j = 0; j < list.size(); j++) {
                    CarData a = list.get(j);
                    if (a.changeStep == i) {
                        boolean ok = cross.changeLane(objects.get(j), a.fromRoad, a.changeLane);
                        result.append("шаг ").append(i).append(", перестроение машины ")
                                .append(a.id).append(ok ? " удалось\n" : " не удалось\n");
                    }
                }
                cross.step();
            }
            result.append("шагов: ").append(steps).append("\n").append(cross.view());
        } else {
            Roundabout round = new Roundabout(s.roads, s.lanes, s.size);
            ArrayList<Car> objects = new ArrayList<>();
            for (CarData a : list) {
                Car car = new Car(a.id, a.tip, a.len, a.lane, a.pos);
                objects.add(car);
                if (!round.add(car, a.fromRoad, a.toRoad)) {
                    result.append("машина ").append(a.id).append(" не добавлена\n");
                }
            }
            for (int i = 1; i <= steps; i++) {
                round.step();
                for (int j = 0; j < list.size(); j++) {
                    CarData a = list.get(j);
                    if (a.changeStep == i) {
                        boolean ok = round.changeLane(objects.get(j), a.changeLane);
                        result.append("шаг ").append(i).append(", перестроение машины ")
                                .append(a.id).append(ok ? " удалось\n" : " не удалось\n");
                    }
                }
            }
            result.append("шагов: ").append(steps).append("\n").append(round.view());
        }
        return result.toString();
    }
}
