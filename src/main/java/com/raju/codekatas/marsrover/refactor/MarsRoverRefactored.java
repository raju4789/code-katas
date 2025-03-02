package com.raju.codekatas.marsrover.refactor;

import com.raju.codekatas.marsrover.refactor.command.RoverCommand;
import com.raju.codekatas.marsrover.refactor.direction.Direction;
import com.raju.codekatas.marsrover.refactor.exception.InvalidCommandException;
import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.factory.CommandFactory;
import com.raju.codekatas.marsrover.refactor.factory.DirectionFactory;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.strategy.MovementStrategy;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarsRoverRefactored {
    private static final Logger logger = LoggerFactory.getLogger(MarsRoverRefactored.class);

    private final MovementValidator movementValidator;
    private final MovementStrategy movementStrategy;
    private final int stepLength; // The number of steps the rover moves in each command
    private Coordinate position; // The current position of the rover
    private Direction direction; // The current direction the rover is facing
    private boolean obstacleEncountered = false;

    public MarsRoverRefactored(MovementValidator movementValidator, MovementStrategy movementStrategy, Coordinate initialPosition, String initialDirection, int stepLength) {
        this.movementValidator = movementValidator;
        this.movementStrategy = movementStrategy;
        this.position = initialPosition;
        this.direction = DirectionFactory.getDirection(initialDirection);
        this.stepLength = stepLength;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isObstacleEncountered() {
        return obstacleEncountered;
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public MovementValidator getMovementValidator() {
        return movementValidator;
    }

    public int getStepLength() {
        return stepLength;
    }

    public void move(String commands) {
        if (commands == null || commands.isEmpty()) {
            logger.error("Commands cannot be null or empty");
            throw new InvalidCommandException("Commands cannot be null or empty");
        }

        CommandFactory commandFactory = new CommandFactory(this);

        for (char commandChar : commands.toCharArray()) {
            if (obstacleEncountered) {
                logger.warn("Obstacle encountered. Stopping further processing of commands.");
                break;
            }

            try {
                RoverCommand command = commandFactory.getCommand(commandChar);
                command.execute();
            } catch (ObstacleException e) {
                logger.error("Obstacle encountered at {}. Stopping further processing of commands.", position);
                obstacleEncountered = true;
                break;
            }
        }
    }

}