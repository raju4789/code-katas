package com.raju.codekatas.marsrover.refactor.factory;

import com.raju.codekatas.marsrover.refactor.direction.*;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;

public class DirectionFactory {
    public static Direction getDirection(String direction, ObstacleDetector obstacleDetector) {
        switch (direction) {
            case "N":
                return new North(obstacleDetector);
            case "E":
                return new East(obstacleDetector);
            case "S":
                return new South(obstacleDetector);
            case "W":
                return new West(obstacleDetector);
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
    }
}
