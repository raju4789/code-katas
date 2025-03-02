package com.raju.codekatas.marsrover.refactor.validator;

import com.raju.codekatas.marsrover.refactor.model.Coordinate;
import com.raju.codekatas.marsrover.refactor.utils.ObstacleDetector;

public class ObstacleMovementValidator implements MovementValidator {
    private final ObstacleDetector obstacleDetector;

    public ObstacleMovementValidator(ObstacleDetector obstacleDetector) {
        this.obstacleDetector = obstacleDetector;
    }

    @Override
    public boolean isMovementValid(Coordinate coordinate) {
        return !obstacleDetector.isObstacle(coordinate);
    }
}
