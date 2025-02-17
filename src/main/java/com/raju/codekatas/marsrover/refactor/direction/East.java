package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.enums.DirectionEnum;

public class East implements Direction {


    public String toString() {
        return "E";
    }

    @Override
    public Direction turnLeft() {
        return new North();
    }

    @Override
    public Direction turnRight() {
        return new South();
    }

    @Override
    public DirectionEnum getDirection() {
        return DirectionEnum.EAST;
    }


}
