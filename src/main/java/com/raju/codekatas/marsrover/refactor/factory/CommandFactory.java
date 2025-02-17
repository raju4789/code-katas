package com.raju.codekatas.marsrover.refactor.factory;

import com.raju.codekatas.marsrover.refactor.MarsRoverRefactored;
import com.raju.codekatas.marsrover.refactor.command.*;
import com.raju.codekatas.marsrover.refactor.exception.InvalidCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private static final Logger logger = LoggerFactory.getLogger(CommandFactory.class);

    private final Map<Character, RoverCommand> commandMap = new HashMap<>();

    public CommandFactory(MarsRoverRefactored rover) {
        commandMap.put('F', new MoveForwardCommand(rover));
        commandMap.put('B', new MoveBackwardCommand(rover));
        commandMap.put('L', new TurnLeftCommand(rover));
        commandMap.put('R', new TurnRightCommand(rover));
    }

    public RoverCommand getCommand(char commandChar) {
        RoverCommand command = commandMap.get(commandChar);
        if (command == null) {
            logger.error("Invalid command: {}", commandChar);
            throw new InvalidCommandException("Invalid command: " + commandChar);
        }
        return command;
    }
}
