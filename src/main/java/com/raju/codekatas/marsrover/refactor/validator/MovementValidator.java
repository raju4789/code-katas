package com.raju.codekatas.marsrover.refactor.validator;

import com.raju.codekatas.marsrover.refactor.model.Coordinate;

public interface MovementValidator {

    boolean isMovementValid(Coordinate coordinate);
}
