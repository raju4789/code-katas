package com.raju.codekatas.marsrover.basic;

import com.raju.codekatas.marsrover.utils.ApplicationConstants;

import java.util.HashSet;
import java.util.Set;

public class MarsRover {

    private int x;
    private int y;
    private String direction;
    private Set<String> obstacles = new HashSet<>();
    private boolean obstacleEncountered = false; // To store the position of the obstacle

    public MarsRover(int x, int y, String direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void setObstacles(Set<String> obstacles) {
        this.obstacles = obstacles;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDirection() {
        return direction;
    }

    public boolean getObstacleEncountered() {
        return obstacleEncountered;
    }

    public void move(String commands) {
        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException("Commands cannot be null or empty");
        }

        for (char command : commands.toCharArray()) {
            if (obstacleEncountered) {
                // Stop processing further commands if an obstacle was encountered
                break;
            }

            switch (command) {
                case 'L':
                    turnLeft();
                    break;
                case 'R':
                    turnRight();
                    break;
                case 'F':
                    moveForward();
                    break;
                case 'B':
                    moveBackward();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command");
            }
        }
    }

    private void turnLeft() {
        switch (direction) {
            case "N":
                direction = "W";
                break;
            case "W":
                direction = "S";
                break;
            case "S":
                direction = "E";
                break;
            case "E":
                direction = "N";
                break;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
    }

    private void turnRight() {
        switch (direction) {
            case "N":
                direction = "E";
                break;
            case "E":
                direction = "S";
                break;
            case "S":
                direction = "W";
                break;
            case "W":
                direction = "N";
                break;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
    }

    private boolean isObstacle(int x, int y) {
        return obstacles.contains(x + "," + y);
    }

    private void moveForward() {
        int newX = x, newY = y;
        switch (direction) {
            case "N":
                newY = (y < ApplicationConstants.MAX_Y) ? y + 1 : 0;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newY = y; // Reset the y-coordinate
                }
                break;
            case "E":
                newX = (x < ApplicationConstants.MAX_X - 1) ? x + 1 : 0;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newX = x; // Reset the x-coordinate
                }
                break;
            case "S":
                newY = (y > 0) ? y - 1 : ApplicationConstants.MAX_Y - 1;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newY = y; // Reset the y-coordinate
                }
                break;
            case "W":
                newX = (x > 0) ? x - 1 : ApplicationConstants.MAX_X - 1;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newX = x; // Reset the x-coordinate
                }
                break;
        }

        x = newX;
        y = newY;

    }

    private void moveBackward() {
        int newX = x, newY = y;
        switch (direction) {
            case "N":
                newY = (y > 0) ? y - 1 : ApplicationConstants.MAX_Y - 1;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newY = y; // Reset the y-coordinate
                }
                break;
            case "E":
                newX = (x > 0) ? x - 1 : ApplicationConstants.MAX_X - 1;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newX = x; // Reset the x-coordinate
                }
                break;
            case "S":
                newY = (y < ApplicationConstants.MAX_Y) ? y + 1 : 0;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newY = y; // Reset the y-coordinate
                }
                break;
            case "W":
                newX = (x < ApplicationConstants.MAX_X - 1) ? x + 1 : 0;
                if (isObstacle(newX, newY)) {
                    obstacleEncountered = true;
                    newX = x; // Reset the x-coordinate
                }
                break;
        }

        x = newX;
        y = newY;
    }
}