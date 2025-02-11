package com.raju.codekatas.marsrover.refactor.factory;

import com.raju.codekatas.marsrover.refactor.direction.*;

public class DirectionFactory {
    public static Direction getDirection(String direction) {
        switch (direction) {
            case "N":
                return new North();
            case "E":
                return new East();
            case "S":
                return new South();
            case "W":
                return new West();
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
    }
}
