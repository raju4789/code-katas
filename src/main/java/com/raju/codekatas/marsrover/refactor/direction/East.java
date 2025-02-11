package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;
import com.raju.codekatas.marsrover.utils.ApplicationConstants;

public class East implements Direction {

    private final ObstacleDetector obstacleDetector;

    public East(ObstacleDetector obstacleDetector) {
        this.obstacleDetector = obstacleDetector;
    }

    public String toString() {
        return "E";
    }

    @Override
    public Direction turnLeft() {
        return new North(obstacleDetector);
    }

    @Override
    public Direction turnRight() {
        return new South(obstacleDetector);
    }

    @Override
    public Coordinate moveForward(Coordinate currentPosition, int stepLength) {
        return calculateNewPosition(currentPosition, stepLength);
    }

    @Override
    public Coordinate moveBackward(Coordinate currentPosition, int stepSize) {
        return calculateNewPosition(currentPosition, -stepSize);
    }

    private Coordinate calculateNewPosition(Coordinate currentPosition, int stepSize) throws ObstacleException {
        int newX = (currentPosition.getX() + stepSize + ApplicationConstants.MAX_X) % ApplicationConstants.MAX_X;
        Coordinate newCoordinate = new Coordinate(newX, currentPosition.getY());
        if (obstacleDetector.isObstacle(newCoordinate)) {
            newCoordinate = currentPosition;
            throw new ObstacleException("Obstacle detected at " + newCoordinate);
        }
        return newCoordinate;
    }

}
