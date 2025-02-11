package com.raju.codekatas.marsrover.refactor.command;

import com.raju.codekatas.marsrover.refactor.MarsRoverRefactored;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurnRightCommand implements RoverCommand {
    private static final Logger logger = LoggerFactory.getLogger(TurnRightCommand.class);

    private final MarsRoverRefactored rover;

    public TurnRightCommand(MarsRoverRefactored rover) {
        this.rover = rover;
    }

    @Override
    public void execute() {
        logger.debug("Turning right");
        rover.setDirection(rover.getDirection().turnRight());
    }
}
