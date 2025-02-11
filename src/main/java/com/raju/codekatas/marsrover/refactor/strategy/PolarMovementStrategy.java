package com.raju.codekatas.marsrover.refactor.strategy;

import com.raju.codekatas.marsrover.refactor.direction.Direction;
import com.raju.codekatas.marsrover.refactor.exception.InvalidCommandException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;

import static com.raju.codekatas.marsrover.utils.ApplicationConstants.MAX_X;
import static com.raju.codekatas.marsrover.utils.ApplicationConstants.MAX_Y;

public class PolarMovementStrategy implements MovementStrategy {

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
                newY = polarWrapLatitude(newY + stepSize);
                break;
            case SOUTH:
                newY = polarWrapLatitude(newY - stepSize);
                break;
            case EAST:
                newX = wrapLongitude(newX + stepSize);
                break;
            case WEST:
                newX = wrapLongitude(newX - stepSize);
                break;
            default:
                throw new InvalidCommandException("Invalid direction: " + currentDirection.getDirection());
        }

        return new Coordinate(newX, newY);
    }

    private int wrapLongitude(int value) {
        return (value % MAX_X + MAX_X) % MAX_X == 0 ? MAX_X : (value % MAX_X + MAX_X) % MAX_X;
    }

    private int polarWrapLatitude(int value) {
        if (value > MAX_Y) {
            return MAX_Y - (value - MAX_Y);
        } else if (value < 1) {
            return 1 + (1 - value);
        }
        return value;
    }
}