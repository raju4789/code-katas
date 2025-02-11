package com.raju.codekatas.marsrover.refactor.strategy;

import com.raju.codekatas.marsrover.refactor.direction.Direction;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;

public interface MovementStrategy {
    Coordinate moveForward(Coordinate currentPosition, Direction currentDirection, int stepSize);

    Coordinate moveBackward(Coordinate currentPosition, Direction currentDirection, int stepSize);
}
