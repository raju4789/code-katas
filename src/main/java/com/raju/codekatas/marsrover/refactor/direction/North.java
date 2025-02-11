package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;
import com.raju.codekatas.marsrover.utils.ApplicationConstants;

public class North implements Direction {

    private final ObstacleDetector obstacleDetector;

    public North(ObstacleDetector obstacleDetector) {
        this.obstacleDetector = obstacleDetector;
    }

    @Override
    public Direction turnLeft() {
        return new West(obstacleDetector);
    }

    @Override
    public Direction turnRight() {
        return new East(obstacleDetector);
    }

    public String toString() {
        return "N";
    }

    @Override
    public Coordinate moveForward(Coordinate currentPosition, int stepSize) throws ObstacleException {
        return calculateNewPosition(currentPosition, stepSize);
    }

    @Override
    public Coordinate moveBackward(Coordinate currentPosition, int stepSize) {
        return calculateNewPosition(currentPosition, -stepSize);
    }

    private Coordinate calculateNewPosition(Coordinate currentPosition, int stepSize) throws ObstacleException {
        int newY = (currentPosition.getY() + stepSize + ApplicationConstants.MAX_Y) % ApplicationConstants.MAX_Y;
        Coordinate newCoordinate = new Coordinate(currentPosition.getX(), newY);
        if (obstacleDetector.isObstacle(newCoordinate)) {
            newCoordinate = currentPosition;
            throw new ObstacleException("Obstacle detected at " + newCoordinate);
        }
        return newCoordinate;
    }
}
