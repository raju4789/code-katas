package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;

public interface Direction {
    Direction turnLeft();

    Direction turnRight();

    Coordinate moveForward(Coordinate currentPosition, int stepLength, MovementValidator movementValidator) throws ObstacleException;

    Coordinate moveBackward(Coordinate currentPosition, int stepSize, MovementValidator movementValidator) throws ObstacleException;
}
