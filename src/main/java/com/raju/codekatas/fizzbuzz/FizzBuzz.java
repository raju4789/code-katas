package com.raju.codekatas.fizzbuzz;

public class FizzBuzz {


    public static String of(int number) {
        if (number == SupportedNumberType.Zero.getValue()) {
            return String.valueOf(SupportedNumberType.Zero.getValue());
        }
        return ofNum(number);
    }

    private static String ofNum(int number) {
        StringBuilder result = new StringBuilder();
        if (isMultipleOf(number, SupportedNumberType.Three.getValue())) {
            result.append(FizzBuzzType.FIZZ);
        }

        if (isMultipleOf(number, SupportedNumberType.Five.getValue())) {
            result.append(FizzBuzzType.BUZZ);
        }

        return result.isEmpty() ? String.valueOf(number) : result.toString();
    }

    private static boolean isMultipleOf(int num, int multiple) {
        return num % multiple == 0;
    }
}
