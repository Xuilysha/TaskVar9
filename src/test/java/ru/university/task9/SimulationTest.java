package ru.university.task9;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {
    @Test
    void roadDoesNotPutTwoCarsInOneCell() {
        Road road = new Road(2, 5);
        Car a = new Car(1, "легковая", 2, 0, 0);
        Car b = new Car(2, "грузовая", 2, 0, 1);
        assertTrue(road.add(a));
        assertFalse(road.add(b));
        assertTrue(road.changeLane(a, 1));
        assertTrue(road.add(b));
        road.step();
        assertEquals(1, a.pos);
        assertEquals(2, b.pos);
    }

    @Test
    void crossroadMovesCar() {
        Crossroad cross = new Crossroad(4, 1, 3);
        assertTrue(cross.add(new Car(1, "легковая", 1, 0, 2), 0, "прямо"));
        for (int i = 0; i < 10; i++) cross.step();
        assertTrue(cross.view().contains("выезд 3"));
        assertTrue(cross.out[2].cars.size() > 0);
    }

    @Test
    void roundaboutMovesCar() {
        Roundabout round = new Roundabout(4, 1, 3);
        assertTrue(round.add(new Car(1, "легковая", 1, 0, 2), 0, 1));
        for (int i = 0; i < 15; i++) round.step();
        assertTrue(round.out[1].cars.size() > 0);
    }
}
