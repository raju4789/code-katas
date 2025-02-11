package com.raju.codekatas.marsrover.refactor;

import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;
import com.raju.codekatas.marsrover.refactor.validator.ObstacleMovementValidator;
import com.raju.codekatas.marsrover.utils.ApplicationConstants;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MarsRoverRefactoredTest {

    @Test
    void testInitialization() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        assertEquals(startPosition, rover.getPosition());
        assertEquals("N", rover.getDirection().toString());
        assertFalse(rover.isObstacleEncountered());
    }

    @Test
    void testTurnLeft() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("L");
        assertEquals("W", rover.getDirection().toString());

        rover.move("L");
        assertEquals("S", rover.getDirection().toString());

        rover.move("L");
        assertEquals("E", rover.getDirection().toString());

        rover.move("L");
        assertEquals("N", rover.getDirection().toString());
    }

    @Test
    void testTurnRight() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("R");
        assertEquals("E", rover.getDirection().toString());

        rover.move("R");
        assertEquals("S", rover.getDirection().toString());

        rover.move("R");
        assertEquals("W", rover.getDirection().toString());

        rover.move("R");
        assertEquals("N", rover.getDirection().toString());
    }

    @Test
    void testMoveForward() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("F");
        assertEquals(new Coordinate(0, 1), rover.getPosition());

        rover.move("R");
        rover.move("F");
        assertEquals(new Coordinate(1, 1), rover.getPosition());

        rover.move("R");
        rover.move("F");
        assertEquals(new Coordinate(1, 0), rover.getPosition());

        rover.move("R");
        rover.move("F");
        assertEquals(new Coordinate(0, 0), rover.getPosition());
    }

    @Test
    void testMoveBackward() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("B");
        assertEquals("N", rover.getDirection().toString());
        assertEquals(new Coordinate(0, ApplicationConstants.MAX_Y - 1), rover.getPosition());

        rover.move("R");
        rover.move("B");
        assertEquals("E", rover.getDirection().toString());
        assertEquals(new Coordinate(ApplicationConstants.MAX_X - 1, ApplicationConstants.MAX_Y - 1), rover.getPosition());

        rover.move("R");
        rover.move("B");
        assertEquals("S", rover.getDirection().toString());
        assertEquals(new Coordinate(ApplicationConstants.MAX_X - 1, 0), rover.getPosition());

        rover.move("R");
        rover.move("B");
        assertEquals("W", rover.getDirection().toString());
        assertEquals(new Coordinate(0, 0), rover.getPosition());
    }

    @Test
    void testObstacleEncountered() {
        Coordinate startPosition = new Coordinate(0, 0);

        Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
        ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
        MovementValidator movementValidator = new ObstacleMovementValidator(new ObstacleDetector(obstacles));

        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("F");
        assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
        assertTrue(rover.isObstacleEncountered());
    }

    @Test
    void testMultipleCommands() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("FFRFF");
        assertEquals(new Coordinate(2, 2), rover.getPosition());
        assertEquals("E", rover.getDirection().toString());
        assertFalse(rover.isObstacleEncountered());
    }

    @Test
    void testObstacleStopsFurtherCommands() {
        Coordinate startPosition = new Coordinate(0, 0);

        Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
        ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
        MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);

        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        rover.move("FFRFF");
        assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should stop at the obstacle
        assertTrue(rover.isObstacleEncountered());
        assertEquals("N", rover.getDirection().toString()); // Should not turn after encountering the obstacle
    }

    @Test
    void testInvalidCommand() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> rover.move("X"));
        assertEquals("Invalid command", exception.getMessage());
    }

    @Test
    void testNullCommands() {
        Coordinate startPosition = new Coordinate(0, 0);

        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> rover.move(null));
        assertEquals("Commands cannot be null or empty", exception.getMessage());
    }

    @Test
    void testEmptyCommands() {
        Coordinate startPosition = new Coordinate(0, 0);
        ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
        MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
        MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, startPosition, "N", 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> rover.move(""));
        assertEquals("Commands cannot be null or empty", exception.getMessage());
    }
}