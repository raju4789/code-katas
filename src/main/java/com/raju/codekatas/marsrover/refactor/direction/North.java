package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.enums.DirectionEnum;

public class North implements Direction {


    @Override
    public Direction turnLeft() {
        return new West();
    }

    @Override
    public Direction turnRight() {
        return new East();
    }

    @Override
    public DirectionEnum getDirection() {
        return DirectionEnum.NORTH;
    }

    public String toString() {
        return "N";
    }


}
