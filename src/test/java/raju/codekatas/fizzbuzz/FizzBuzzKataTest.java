package raju.codekatas.fizzbuzz;


import com.raju.codekatas.fizzbuzz.FizzBuzzKata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FizzBuzzKataTest {

    @Test
    @DisplayName("should return 0 when input is 0")
    void test1() {
        assertEquals("0", FizzBuzzKata.of(0));
    }

    @Test
    @DisplayName("should return 1 when input is 1")
    void test2() {
        assertEquals("1", FizzBuzzKata.of(1));
    }

    @Test
    @DisplayName("should return Fizz when input is multiple of 3")
    void test3() {
        assertEquals("Fizz", FizzBuzzKata.of(3));
        assertEquals("Fizz", FizzBuzzKata.of(6));
        assertEquals("Fizz", FizzBuzzKata.of(18));
    }

    @Test
    @DisplayName("should return Buzz when input is multiple of 5")
    void test4() {
        assertEquals("Buzz", FizzBuzzKata.of(5));
        assertEquals("Buzz", FizzBuzzKata.of(10));
        assertEquals("Buzz", FizzBuzzKata.of(20));
    }

    @Test
    @DisplayName("should return FizzBuzz when input is multiple of 3 and 5")
    void test5() {
        assertEquals("FizzBuzz", FizzBuzzKata.of(15));
        assertEquals("FizzBuzz", FizzBuzzKata.of(30));
        assertEquals("FizzBuzz", FizzBuzzKata.of(45));
    }

}
