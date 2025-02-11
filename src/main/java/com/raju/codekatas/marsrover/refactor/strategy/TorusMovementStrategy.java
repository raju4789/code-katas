package com.raju.codekatas.marsrover.refactor.strategy;

import com.raju.codekatas.marsrover.refactor.direction.Direction;
import com.raju.codekatas.marsrover.refactor.exception.InvalidCommandException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;

import static com.raju.codekatas.marsrover.utils.ApplicationConstants.MAX_X;
import static com.raju.codekatas.marsrover.utils.ApplicationConstants.MAX_Y;

public class TorusMovementStrategy implements MovementStrategy {

    @Override
    public Coordinate moveForward(Coordinate currentPosition, Direction currentDirection, int stepSize) {
        return calculateNewPosition(currentPosition, currentDirection, stepSize);
    }

    @Override
    public Coordinate moveBackward(Coordinate currentPosition, Direction currentDirection, int stepSize) {
        return calculateNewPosition(currentPosition, currentDirection, -stepSize);
    }

    private Coordinate calculateNewPosition(Coordinate currentPosition, Direction currentDirection, int stepSize) {
        int newX = currentPosition.getX();
        int newY = currentPosition.getY();

        switch (currentDirection.getDirection()) {
            case NORTH:
                newY = wrapCoordinate(newY + stepSize, MAX_Y);
                break;
            case EAST:
                newX = wrapCoordinate(newX + stepSize, MAX_X);
                break;
            case SOUTH:
                newY = wrapCoordinate(newY - stepSize, MAX_Y);
                break;
            case WEST:
                newX = wrapCoordinate(newX - stepSize, MAX_X);
                break;
            default:
                throw new InvalidCommandException("Invalid direction: " + currentDirection.getDirection());
        }

        return new Coordinate(newX, newY);
    }

    private int wrapCoordinate(int value, int max) {
        return (value % max + max) % max; // Ensures the value wraps correctly for both positive and negative values
    }
}