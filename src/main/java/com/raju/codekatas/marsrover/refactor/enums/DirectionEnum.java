package com.raju.codekatas.marsrover.refactor.enums;

public enum DirectionEnum {
    NORTH("N"),
    EAST("E"),
    SOUTH("S"),
    WEST("W");

    private final String code;

    DirectionEnum(String code) {
        this.code = code;
    }

    public static DirectionEnum fromString(String direction) {
        for (DirectionEnum dir : DirectionEnum.values()) {
            if (dir.getCode().equalsIgnoreCase(direction)) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    public String getCode() {
        return code;
    }
}