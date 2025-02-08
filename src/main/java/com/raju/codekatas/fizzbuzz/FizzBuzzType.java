package com.raju.codekatas.fizzbuzz;

public enum FizzBuzzType {
    FIZZ("Fizz"),
    BUZZ("Buzz");

    private final String value;

    FizzBuzzType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
