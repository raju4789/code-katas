package com.raju.codekatas.marsrover;

import com.raju.codekatas.marsrover.basic.MarsRover;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MarsRoverTest {

    @Test
    void testInitialization() {
        MarsRover rover = new MarsRover(0, 0, "N");
        assertEquals(0, rover.getX());
        assertEquals(0, rover.getY());
        assertEquals("N", rover.getDirection());
    }

    @Test
    void testInitializationWithNonZeroPosition() {
        MarsRover rover = new MarsRover(5, 5, "E");
        assertEquals(5, rover.getX());
        assertEquals(5, rover.getY());
        assertEquals("E", rover.getDirection());
    }

    @Test
    void testTurnLeftFromNorth() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.move("L");
        assertEquals("W", rover.getDirection());
    }

    @Test
    void testTurnLeftFromWest() {
        MarsRover rover = new MarsRover(0, 0, "W");
        rover.move("L");
        assertEquals("S", rover.getDirection());
    }

    @Test
    void testTurnLeftFromSouth() {
        MarsRover rover = new MarsRover(0, 0, "S");
        rover.move("L");
        assertEquals("E", rover.getDirection());
    }

    @Test
    void testTurnLeftFromEast() {
        MarsRover rover = new MarsRover(0, 0, "E");
        rover.move("L");
        assertEquals("N", rover.getDirection());
    }

    @Test
    void testTurnRightFromNorth() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.move("R");
        assertEquals("E", rover.getDirection());
    }

    @Test
    void testTurnRightFromEast() {
        MarsRover rover = new MarsRover(0, 0, "E");
        rover.move("R");
        assertEquals("S", rover.getDirection());
    }

    @Test
    void testTurnRightFromSouth() {
        MarsRover rover = new MarsRover(0, 0, "S");
        rover.move("R");
        assertEquals("W", rover.getDirection());
    }

    @Test
    void testTurnRightFromWest() {
        MarsRover rover = new MarsRover(0, 0, "W");
        rover.move("R");
        assertEquals("N", rover.getDirection());
    }

    @Test
    void testMoveForwardFacingNorth() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.move("F");
        assertEquals(0, rover.getX());
        assertEquals(1, rover.getY()); // Wrapping at edge
    }

    @Test
    void testMoveForwardFacingEast() {
        MarsRover rover = new MarsRover(0, 0, "E");
        rover.move("F");
        assertEquals(1, rover.getX());
        assertEquals(0, rover.getY());
    }

    @Test
    void testMoveForwardFacingSouth() {
        MarsRover rover = new MarsRover(0, 0, "S");
        rover.move("F");
        assertEquals(0, rover.getX());
        assertEquals(4, rover.getY());
    }

    @Test
    void testMoveForwardFacingWest() {
        MarsRover rover = new MarsRover(1, 0, "W");
        rover.move("F");
        assertEquals(0, rover.getX());
        assertEquals(0, rover.getY());
    }

    @Test
    void testMoveBackwardFacingNorth() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.move("B");
        assertEquals(0, rover.getX());
        assertEquals(4, rover.getY());
    }

    @Test
    void testMoveBackwardFacingEast() {
        MarsRover rover = new MarsRover(1, 0, "E");
        rover.move("B");
        assertEquals(0, rover.getX());
        assertEquals(0, rover.getY());
    }

    @Test
    void testMoveBackwardFacingSouth() {
        MarsRover rover = new MarsRover(0, 1, "S");
        rover.move("B");
        assertEquals(0, rover.getX());
        assertEquals(2, rover.getY());
    }

    @Test
    void testMoveBackwardFacingWest() {
        MarsRover rover = new MarsRover(0, 0, "W");
        rover.move("B");
        assertEquals(1, rover.getX());
        assertEquals(0, rover.getY());
    }

    @Test
    void testCombinedCommands() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.move("FFLFF");
        assertEquals(3, rover.getX());
        assertEquals(2, rover.getY());
        assertEquals("W", rover.getDirection());
    }

    @Test
    void testObstacleEncountered() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.setObstacles(Set.of("0,1")); // Obstacle at (0,1)

        rover.move("FFRFF");

        // Verify the rover's final position
        assertEquals(0, rover.getX());
        assertEquals(0, rover.getY()); // Last valid position before obstacle

        // Verify the rover's direction (unchanged because it stopped before turning)
        assertEquals("N", rover.getDirection());

        // Verify the obstacle encountered
        assertEquals(true, rover.getObstacleEncountered());
    }

    @Test
    void testMultipleObstacles() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.setObstacles(Set.of("3,1", "2,2")); // Obstacles at (0,1) and (2,2)

        rover.move("FFRFF");

        // Verify the rover's final position
        assertEquals(1, rover.getX());
        assertEquals(2, rover.getY()); // Last valid position before obstacle

        // Verify the obstacle encountered
        assertEquals(true, rover.getObstacleEncountered());
    }

    @Test
    void testNoObstacles() {
        MarsRover rover = new MarsRover(0, 0, "N");
        rover.move("FFRFF");

        // Verify the rover's final position
        assertEquals(2, rover.getX());
        assertEquals(2, rover.getY());
        assertEquals("E", rover.getDirection());

        // Verify no obstacle encountered
        assertFalse(rover.getObstacleEncountered());
    }

    @Test
    void testInvalidCommand() {
        MarsRover rover = new MarsRover(0, 0, "N");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> rover.move("X"));
        assertEquals("Invalid command", exception.getMessage());
    }

    @Test
    void testNullCommands() {
        MarsRover rover = new MarsRover(0, 0, "N");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> rover.move(null));
        assertEquals("Commands cannot be null or empty", exception.getMessage());
    }

    @Test
    void testEmptyCommands() {
        MarsRover rover = new MarsRover(0, 0, "N");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> rover.move(""));
        assertEquals("Commands cannot be null or empty", exception.getMessage());
    }
}