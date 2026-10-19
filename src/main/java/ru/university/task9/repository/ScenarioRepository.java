package ru.university.task9.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import ru.university.task9.model.CarData;
import ru.university.task9.model.Scenario;

public class ScenarioRepository {
    private String url;
    private String user;
    private String pass;

    public ScenarioRepository(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public void init() throws SQLException {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS scenarios (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, kind VARCHAR(20) NOT NULL, " +
                    "roads INTEGER NOT NULL, lanes INTEGER NOT NULL, size INTEGER NOT NULL, " +
                    "stop_road INTEGER NOT NULL DEFAULT -1)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS cars (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "scenario_id INTEGER NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE, " +
                    "tip VARCHAR(50) NOT NULL, len INTEGER NOT NULL, lane INTEGER NOT NULL, " +
                    "pos INTEGER NOT NULL, from_road INTEGER NOT NULL, to_road INTEGER NOT NULL, " +
                    "turn_name VARCHAR(20) NOT NULL, change_step INTEGER NOT NULL DEFAULT 0, " +
                    "change_lane INTEGER NOT NULL DEFAULT -1)");
            s.executeUpdate("ALTER TABLE scenarios ADD COLUMN IF NOT EXISTS stop_road INTEGER NOT NULL DEFAULT -1");
            s.executeUpdate("ALTER TABLE cars ADD COLUMN IF NOT EXISTS change_step INTEGER NOT NULL DEFAULT 0");
            s.executeUpdate("ALTER TABLE cars ADD COLUMN IF NOT EXISTS change_lane INTEGER NOT NULL DEFAULT -1");
        }
    }

    public int addScenario(Scenario a) throws SQLException {
        String sql = "INSERT INTO scenarios(name, kind, roads, lanes, size, stop_road) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, a.name);
            p.setString(2, a.kind);
            p.setInt(3, a.roads);
            p.setInt(4, a.lanes);
            p.setInt(5, a.size);
            p.setInt(6, a.stopRoad);
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                r.next();
                return r.getInt(1);
            }
        }
    }

    public ArrayList<Scenario> all() throws SQLException {
        ArrayList<Scenario> list = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("SELECT * FROM scenarios ORDER BY id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(readScenario(r));
            }
        }
        return list;
    }

    public Scenario get(int id) throws SQLException {
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("SELECT * FROM scenarios WHERE id = ?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? readScenario(r) : null;
            }
        }
    }

    private Scenario readScenario(ResultSet r) throws SQLException {
        return new Scenario(r.getInt("id"), r.getString("name"), r.getString("kind"),
                r.getInt("roads"), r.getInt("lanes"), r.getInt("size"), r.getInt("stop_road"));
    }

    public void delete(int id) throws SQLException {
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("DELETE FROM scenarios WHERE id = ?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    public int addCar(CarData a) throws SQLException {
        String sql = "INSERT INTO cars(scenario_id, tip, len, lane, pos, from_road, to_road, turn_name, change_step, change_lane) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, a.scenarioId);
            p.setString(2, a.tip);
            p.setInt(3, a.len);
            p.setInt(4, a.lane);
            p.setInt(5, a.pos);
            p.setInt(6, a.fromRoad);
            p.setInt(7, a.toRoad);
            p.setString(8, a.turn);
            p.setInt(9, a.changeStep);
            p.setInt(10, a.changeLane);
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                r.next();
                return r.getInt(1);
            }
        }
    }

    public ArrayList<CarData> cars(int scenarioId) throws SQLException {
        ArrayList<CarData> list = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("SELECT * FROM cars WHERE scenario_id = ? ORDER BY id")) {
            p.setInt(1, scenarioId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(new CarData(r.getInt("id"), r.getInt("scenario_id"), r.getString("tip"),
                            r.getInt("len"), r.getInt("lane"), r.getInt("pos"), r.getInt("from_road"),
                            r.getInt("to_road"), r.getString("turn_name"),
                            r.getInt("change_step"), r.getInt("change_lane")));
                }
            }
        }
        return list;
    }

    public void deleteCar(int id, int scenarioId) throws SQLException {
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("DELETE FROM cars WHERE id = ? AND scenario_id = ?")) {
            p.setInt(1, id);
            p.setInt(2, scenarioId);
            p.executeUpdate();
        }
    }
}
