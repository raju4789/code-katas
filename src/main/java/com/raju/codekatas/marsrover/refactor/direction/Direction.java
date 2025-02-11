package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;

public interface Direction {
    Direction turnLeft();

    Direction turnRight();

    Coordinate moveForward(Coordinate currentPosition, int stepLength) throws ObstacleException;

    Coordinate moveBackward(Coordinate currentPosition, int stepSize) throws ObstacleException;
}
