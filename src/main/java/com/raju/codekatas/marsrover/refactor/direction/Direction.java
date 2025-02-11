package com.raju.codekatas.marsrover.refactor.direction;

import com.raju.codekatas.marsrover.refactor.enums.DirectionEnum;

public interface Direction {
    Direction turnLeft();

    Direction turnRight();

    DirectionEnum getDirection();

}
