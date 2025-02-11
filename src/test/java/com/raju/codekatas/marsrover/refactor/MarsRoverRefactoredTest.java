package com.raju.codekatas.marsrover.refactor;

import com.raju.codekatas.marsrover.refactor.exception.InvalidCommandException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.strategy.MovementStrategy;
import com.raju.codekatas.marsrover.refactor.strategy.PolarMovementStrategy;
import com.raju.codekatas.marsrover.refactor.strategy.TorusMovementStrategy;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;
import com.raju.codekatas.marsrover.refactor.validator.ObstacleMovementValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.raju.codekatas.marsrover.utils.ApplicationConstants.MAX_X;
import static com.raju.codekatas.marsrover.utils.ApplicationConstants.MAX_Y;
import static org.junit.jupiter.api.Assertions.*;

class MarsRoverRefactoredTest {

    @Nested
    class CommonTests {

        @Test
        @DisplayName("Test initialization of MarsRover")
        void testInitialization() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            assertEquals(startPosition, rover.getPosition());
            assertEquals("N", rover.getDirection().toString());
            assertFalse(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test turning left")
        void testTurnLeft() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

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
        @DisplayName("Test turning right")
        void testTurnRight() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

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
        @DisplayName("Test invalid command")
        void testInvalidCommand() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            Exception exception = assertThrows(InvalidCommandException.class, () -> rover.move("X"));
            assertEquals("Invalid command: X", exception.getMessage());
        }

        @Test
        @DisplayName("Test null commands")
        void testNullCommands() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            Exception exception = assertThrows(InvalidCommandException.class, () -> rover.move(null));
            assertEquals("Commands cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Test empty commands")
        void testEmptyCommands() {
            Coordinate startPosition = new Coordinate(0, 0);
            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            Exception exception = assertThrows(InvalidCommandException.class, () -> rover.move(""));
            assertEquals("Commands cannot be null or empty", exception.getMessage());
        }
    }

    @Nested
    class TorusMovementStrategyTests {

        @Test
        @DisplayName("Test moving forward in all directions for TorusMovementStrategy")
        void testMoveForward() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

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
        @DisplayName("Test moving backward in all directions for TorusMovementStrategy")
        void testMoveBackward() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            rover.move("B");
            assertEquals("N", rover.getDirection().toString());
            assertEquals(new Coordinate(0, MAX_Y - 1), rover.getPosition());

            rover.move("R");
            rover.move("B");
            assertEquals("E", rover.getDirection().toString());
            assertEquals(new Coordinate(MAX_X - 1, MAX_Y - 1), rover.getPosition());

            rover.move("R");
            rover.move("B");
            assertEquals("S", rover.getDirection().toString());
            assertEquals(new Coordinate(MAX_X - 1, 0), rover.getPosition());

            rover.move("R");
            rover.move("B");
            assertEquals("W", rover.getDirection().toString());
            assertEquals(new Coordinate(0, 0), rover.getPosition());
        }

        @Test
        @DisplayName("Test encountering obstacles for TorusMovementStrategy")
        void testObstacleEncountered() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
            assertTrue(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple obstacles for TorusMovementStrategy where we hit second obstacle")
        void testMultipleObstacleEncountered() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
            assertTrue(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple obstacles for TorusMovementStrategy where we hit second obstacle")
        void testMultipleObstacles() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1), new Coordinate(1, 0));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "E", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
            assertTrue(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple obstacles for TorusMovementStrategy where we hit no obstacles")
        void testMultipleObstaclesNoObstacle() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(1, 1), new Coordinate(1, 0));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            rover.move("FF");
            assertEquals(new Coordinate(0, 2), rover.getPosition());
            assertFalse(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple commands for TorusMovementStrategy")
        void testMultipleCommands() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            rover.move("FFRFF");
            assertEquals(new Coordinate(2, 2), rover.getPosition());
            assertEquals("E", rover.getDirection().toString());
            assertFalse(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple commands with obstacles for TorusMovementStrategy")
        void testObstacleStopsFurtherCommands() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy torusMovementStrategy = new TorusMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, torusMovementStrategy, startPosition, "N", 1);

            rover.move("FFRFF");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should stop at the obstacle
            assertTrue(rover.isObstacleEncountered());
            assertEquals("N", rover.getDirection().toString()); // Should not turn after encountering the obstacle
        }
    }

    @Nested
    class PolarMovementStrategyTests {

        @Test
        @DisplayName("Test moving forward in all directions for PolarMovementStrategy")
        void testMoveForward() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 1), rover.getPosition());

            rover.move("R");
            rover.move("F");
            assertEquals(new Coordinate(1, 1), rover.getPosition());

            rover.move("R");
            rover.move("F");
            assertEquals(new Coordinate(1, 2), rover.getPosition());

            rover.move("R");
            rover.move("F");
            assertEquals(new Coordinate(5, 2), rover.getPosition());
        }

        @Test
        @DisplayName("Test moving backward in all directions for PolarMovementStrategy")
        void testMoveBackward() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            ObstacleMovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("B");
            assertEquals("N", rover.getDirection().toString());
            assertEquals(new Coordinate(0, 3), rover.getPosition());

            rover.move("R");
            rover.move("B");
            assertEquals("E", rover.getDirection().toString());
            assertEquals(new Coordinate(MAX_X - 1, 3), rover.getPosition());

            rover.move("R");
            rover.move("B");
            assertEquals("S", rover.getDirection().toString());
            assertEquals(new Coordinate(MAX_X - 1, 4), rover.getPosition());

            rover.move("R");
            rover.move("B");
            assertEquals("W", rover.getDirection().toString());
            assertEquals(new Coordinate(MAX_X, MAX_Y - 1), rover.getPosition());
        }

        @Test
        @DisplayName("Test encountering obstacles for PolarMovementStrategy")
        void testObstacleEncountered() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
            assertTrue(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple obstacles for PolarMovementStrategy where we hit second obstacle")
        void testMultipleObstacleEncountered() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
            assertTrue(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple obstacles for PolarMovementStrategy where we hit second obstacle")
        void testMultipleObstacles() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1), new Coordinate(1, 0));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "E", 1);

            rover.move("F");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should not move
            assertTrue(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple obstacles for PolarMovementStrategy where we hit no obstacles")
        void testMultipleObstaclesNoObstacle() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(1, 1), new Coordinate(1, 0));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("FF");
            assertEquals(new Coordinate(0, 2), rover.getPosition());
            assertFalse(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple commands for PolarMovementStrategy")
        void testMultipleCommands() {
            Coordinate startPosition = new Coordinate(0, 0);

            ObstacleDetector obstacleDetector = new ObstacleDetector(Set.of());
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();
            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("FFRFF");
            assertEquals(new Coordinate(2, 2), rover.getPosition());
            assertEquals("E", rover.getDirection().toString());
            assertFalse(rover.isObstacleEncountered());
        }

        @Test
        @DisplayName("Test multiple commands with obstacles for PolarMovementStrategy")
        void testObstacleStopsFurtherCommands() {
            Coordinate startPosition = new Coordinate(0, 0);

            Set<Coordinate> obstacles = Set.of(new Coordinate(0, 1));
            ObstacleDetector obstacleDetector = new ObstacleDetector(obstacles);
            MovementValidator movementValidator = new ObstacleMovementValidator(obstacleDetector);
            MovementStrategy polarMovementStrategy = new PolarMovementStrategy();

            MarsRoverRefactored rover = new MarsRoverRefactored(movementValidator, polarMovementStrategy, startPosition, "N", 1);

            rover.move("FFRFF");
            assertEquals(new Coordinate(0, 0), rover.getPosition()); // Should stop at the obstacle
            assertTrue(rover.isObstacleEncountered());
            assertEquals("N", rover.getDirection().toString()); // Should not turn after encountering the obstacle
        }
    }
}