package com.raju.codekatas.marsrover.refactor;

import com.raju.codekatas.marsrover.refactor.direction.Direction;
import com.raju.codekatas.marsrover.refactor.factory.DirectionFactory;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarsRoverRefactored {
    private static final Logger logger = LoggerFactory.getLogger(MarsRoverRefactored.class);


    private final MovementValidator movementValidator;
    private final int stepLength; // The number of steps the rover moves in each command
    private Coordinate position; // The current position of the rover
    private Direction direction; // The current direction the rover is facing
    private boolean obstacleEncountered = false;

    public MarsRoverRefactored(MovementValidator movementValidator, Coordinate initialPosition, String initialDirection, int stepLength) {
        this.movementValidator = movementValidator;
        this.position = initialPosition;
        this.direction = DirectionFactory.getDirection(initialDirection);
        this.stepLength = stepLength;
    }

    public Coordinate getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isObstacleEncountered() {
        return obstacleEncountered;
    }

    public void move(String commands) {
        if (commands == null || commands.isEmpty()) {
            logger.error("Commands cannot be null or empty");
            throw new IllegalArgumentException("Commands cannot be null or empty");
        }

        for (char command : commands.toCharArray()) {
            if (obstacleEncountered) {
                logger.info("Obstacle encountered. Stopping further processing of commands");
                break;
            }

            switch (command) {
                case 'L':
                    direction = direction.turnLeft();
                    break;
                case 'R':
                    direction = direction.turnRight();
                    break;
                case 'F':
                    try {
                        position = direction.moveForward(position, stepLength, movementValidator);
                    } catch (Exception e) {
                        logger.error("Obstacle encountered at {}", position);
                        obstacleEncountered = true;
                    }
                    break;
                case 'B':
                    try {
                        position = direction.moveBackward(position, stepLength, movementValidator);
                    } catch (Exception e) {
                        logger.error("Obstacle encountered at {}", position);
                        obstacleEncountered = true;
                    }
                    break;
                default:
                    logger.error("Invalid command");
                    throw new IllegalArgumentException("Invalid command");
            }
        }
    }

}