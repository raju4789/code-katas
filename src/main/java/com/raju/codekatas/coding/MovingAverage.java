package com.raju.codekatas.coding;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Moving Average from Data Stream
 * Design a class that calculates the moving average of the last size integers from a stream of incoming data.
 * Your class should support the following operations:
 * Initialization: Given a window size size, set up the moving average calculator.
 * next(val): Add the integer val to the data stream and return the current moving average of the last size values.
 * If there are fewer than size values in the stream so far, the moving average should be computed using all the values available.
 * <p>
 * Examples
 * <p>
 * Example 1:
 * Input:
 * MovingAverage m = new MovingAverage(3);
 * m.next(1);   // Returns 1.0  (average of [1])
 * m.next(10);  // Returns 5.5  (average of [1, 10])
 * m.next(3);   // Returns 4.66667  (average of [1, 10, 3])
 * m.next(5);   // Returns 6.0  (average of [10, 3, 5]) — oldest value 1 is removed
 * <p>
 * Example 2:
 * Input:
 * MovingAverage m = new MovingAverage(1);
 * m.next(5);  // Returns 5.0
 * m.next(10); // Returns 10.0  (window size is 1, so only last value counts)
 * <p>
 * Constraints
 * 1 <= size <= 1000
 * <p>
 * Values passed to next(val) are integers.
 * <p>
 * The number of calls to next() can be very large, so the solution should be efficient.
 */
public class MovingAverage {

    private final int size;

    private Queue<Integer> window = new LinkedList<>();

    private long sum = 0;

    public MovingAverage(int size) {
        this.size = size;
    }

    public static void main(String[] args) {
        MovingAverage movingAverage = new MovingAverage(3);
        System.out.println(movingAverage.next(1));   // Returns 1.0  (average of [1])
        System.out.println(movingAverage.next(10));  // Returns 5.5  (average of [1, 10])
        System.out.println(movingAverage.next(3));   // Returns 4.66667  (average of [1, 10, 3])
        System.out.println(movingAverage.next(5));   // Returns 6.0  (average of [10, 3, 5]) — oldest value 1 is removed
    }

    public double next(int num) {

        window.offer(num);
        sum += num;


        if (window.size() > size) {
            sum -= Objects.requireNonNull(window.poll());
        }

        return (double) sum / window.size();

    }

}
