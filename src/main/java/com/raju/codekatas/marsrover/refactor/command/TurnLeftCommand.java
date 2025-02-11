package com.raju.codekatas.marsrover.refactor.command;

import com.raju.codekatas.marsrover.refactor.MarsRoverRefactored;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurnLeftCommand implements RoverCommand {
    private static final Logger logger = LoggerFactory.getLogger(TurnLeftCommand.class);

    private final MarsRoverRefactored rover;

    public TurnLeftCommand(MarsRoverRefactored rover) {
        this.rover = rover;
    }

    @Override
    public void execute() {
        logger.debug("Turning left");
        rover.setDirection(rover.getDirection().turnLeft());
    }
}
