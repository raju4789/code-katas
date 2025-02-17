package com.raju.codekatas.marsrover.refactor.command;

import com.raju.codekatas.marsrover.refactor.MarsRoverRefactored;
import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveBackwardCommand implements RoverCommand {
    private static final Logger logger = LoggerFactory.getLogger(MoveBackwardCommand.class);

    private final MarsRoverRefactored rover;

    public MoveBackwardCommand(MarsRoverRefactored rover) {
        this.rover = rover;
    }

    @Override
    public void execute() {
        Coordinate newPosition = rover.getMovementStrategy().moveBackward(
                rover.getPosition(), rover.getDirection(), rover.getStepLength()
        );

        if (!rover.getMovementValidator().isMovementValid(newPosition)) {
            throw new ObstacleException("Obstacle detected at " + newPosition);
        }

        logger.debug("Moving backward to {}", newPosition);

        rover.setPosition(newPosition);
    }
}
