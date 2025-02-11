package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.enums.DirectionEnum;

public class South implements Direction {

    public String toString() {
        return "S";
    }

    @Override
    public Direction turnLeft() {
        return new East();
    }

    @Override
    public Direction turnRight() {
        return new West();
    }

    @Override
    public DirectionEnum getDirection() {
        return DirectionEnum.SOUTH;
    }


}
