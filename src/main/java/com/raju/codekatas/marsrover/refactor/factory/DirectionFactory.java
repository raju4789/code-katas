package com.raju.codekatas.marsrover.refactor.factory;

import com.raju.codekatas.marsrover.refactor.direction.*;
import com.raju.codekatas.marsrover.refactor.enums.DirectionEnum;

public class DirectionFactory {
    public static Direction getDirection(String direction) {
        DirectionEnum directionEnum = DirectionEnum.fromString(direction);

        switch (directionEnum) {
            case NORTH:
                return new North();
            case EAST:
                return new East();
            case SOUTH:
                return new South();
            case WEST:
                return new West();
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }
}