package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.validator.MovementValidator;
import com.raju.codekatas.marsrover.utils.ApplicationConstants;

public class West implements Direction {

    public String toString() {
        return "W";
    }

    @Override
    public Direction turnLeft() {
        return new South();
    }

    @Override
    public Direction turnRight() {
        return new North();
    }

    @Override
    public Coordinate moveForward(Coordinate currentPosition, int stepLength, MovementValidator movementValidator) throws ObstacleException {
        return calculateNewPosition(currentPosition, -stepLength, movementValidator);
    }

    @Override
    public Coordinate moveBackward(Coordinate currentPosition, int stepSize, MovementValidator movementValidator) throws ObstacleException {
        return calculateNewPosition(currentPosition, stepSize, movementValidator);
    }

    private Coordinate calculateNewPosition(Coordinate currentPosition, int stepSize, MovementValidator movementValidator) throws ObstacleException {
        int newX = (currentPosition.getX() + stepSize + ApplicationConstants.MAX_X) % ApplicationConstants.MAX_X;

        Coordinate newCoordinate = new Coordinate(newX, currentPosition.getY());

        if (!movementValidator.isMovementValid(newCoordinate)) {
            throw new ObstacleException("Obstacle detected at " + newCoordinate);
        }
        return newCoordinate;
    }
}


