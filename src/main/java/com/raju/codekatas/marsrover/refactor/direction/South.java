package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;
import com.raju.codekatas.marsrover.utils.ApplicationConstants;

public class South implements Direction {

    public String toString() {
        return "S";
    }

    @Override
    public Direction turnLeft() {
        return new East();
    }

    @Override
    public Direction turnRight() {
        return new West();
    }

    @Override
    public Coordinate moveForward(Coordinate currentPosition, int stepSize, MovementValidator movementValidator) throws ObstacleException {
        return calculateNewPosition(currentPosition, -stepSize, movementValidator);
    }

    @Override
    public Coordinate moveBackward(Coordinate currentPosition, int stepSize, MovementValidator movementValidator) throws ObstacleException {
        return calculateNewPosition(currentPosition, stepSize, movementValidator);
    }

    private Coordinate calculateNewPosition(Coordinate currentPosition, int stepSize, MovementValidator movementValidator) throws ObstacleException {
        int newY = (currentPosition.getY() + stepSize + ApplicationConstants.MAX_Y) % ApplicationConstants.MAX_Y;
        Coordinate newCoordinate = new Coordinate(currentPosition.getX(), newY);
        if (!movementValidator.isMovementValid(newCoordinate)) {
            throw new ObstacleException("Obstacle detected at " + newCoordinate);
        }
        return newCoordinate;
    }

}
