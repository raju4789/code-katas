package com.raju.codekatas.fizzbuzz;

public enum SupportedNumberType {
    Zero(0),
    Three(3),
    Five(5);

    private final int value;

    SupportedNumberType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
