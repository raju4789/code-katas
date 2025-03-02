package com.raju.codekatas.marsrover.refactor.utils;

import com.raju.codekatas.marsrover.refactor.model.Coordinate;

import java.util.Set;

public class ObstacleDetector {
    private final Set<Coordinate> obstacles;

    public ObstacleDetector(Set<Coordinate> obstacles) {
        this.obstacles = obstacles;
    }

    public boolean isObstacle(Coordinate coordinate) {
        return obstacles.contains(coordinate);
    }
}
