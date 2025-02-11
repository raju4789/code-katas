package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.exception.ObstacleException;
import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;
import com.raju.codekatas.marsrover.utils.ApplicationConstants;

public class South implements Direction {

    private final ObstacleDetector obstacleDetector;

    public South(ObstacleDetector obstacleDetector) {
        this.obstacleDetector = obstacleDetector;
    }

    public String toString() {
        return "S";
    }

    @Override
    public Direction turnLeft() {
        return new East(obstacleDetector);
    }

    @Override
    public Direction turnRight() {
        return new West(obstacleDetector);
    }

    @Override
    public Coordinate moveForward(Coordinate currentPosition, int stepSize) throws ObstacleException {
        return calculateNewPosition(currentPosition, -stepSize);
    }

    @Override
    public Coordinate moveBackward(Coordinate currentPosition, int stepSize) {
        return calculateNewPosition(currentPosition, stepSize);
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
