package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.enums.DirectionEnum;

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
    public DirectionEnum getDirection() {
        return DirectionEnum.WEST;
    }
}


