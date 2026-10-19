package ru.university.task9;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import ru.university.task9.model.CarData;
import ru.university.task9.repository.ScenarioRepository;
import ru.university.task9.service.SimService;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class PostgresTest {
    @Test
    void dataIsReallySaved() throws Exception {
        String url = System.getenv("TEST_DB_URL");
        assumeTrue(url != null && !url.isEmpty());
        String user = System.getenv("TEST_DB_USER");
        String pass = System.getenv("TEST_DB_PASSWORD");
        ScenarioRepository repo = new ScenarioRepository(url, user == null ? "postgres" : user,
                pass == null ? "" : pass);
        repo.init();
        SimService service = new SimService(repo);
        int id = service.addScenario("тест бд", "cross", 4, 2, 5, 0);
        try {
            service.addCar(new CarData(0, id, "легковая", 1, 0, 0, 0, 0, "прямо", 2, 1));
            ScenarioRepository another = new ScenarioRepository(url, user == null ? "postgres" : user,
                    pass == null ? "" : pass);
            assertEquals("тест бд", another.get(id).name);
            assertEquals(1, another.cars(id).size());
            assertTrue(service.run(id, 3).contains("шагов: 3"));
            assertTrue(service.run(id, 3).contains("знак стоп"));
            assertThrows(IllegalArgumentException.class, () -> service.addCar(
                    new CarData(0, id, "другая", 1, 0, 0, 0, 0, "прямо", 0, -1)));
        } finally {
            service.deleteScenario(id);
        }
        assertNull(repo.get(id));
        assertTrue(repo.cars(id).isEmpty());

        int roundId = service.addScenario("тест кольца", "round", 4, 2, 5, -1);
        try {
            service.addCar(new CarData(0, roundId, "грузовик", 2, 0, 3, 0, 1, null, 2, 1));
            assertEquals(1, repo.cars(roundId).size());
            assertTrue(service.run(roundId, 10).contains("кольцо"));
        } finally {
            service.deleteScenario(roundId);
        }
    }
}
