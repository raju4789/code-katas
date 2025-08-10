package com.raju.codekatas.coding.progressivefiletype;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * IntegerContainer maintains a collection of integers with support for the following operations:
 * <p>
 * 1. add(int value) - Adds an integer value to the container.
 * Returns the new total number of integers in the container after adding.
 * <p>
 * 2. delete(int value) - Deletes one occurrence of the given value from the container if it exists.
 * Returns true if the value was present and deleted, false otherwise.
 * <p>
 * 3. getMedian() - Returns the median integer value from the current collection wrapped in Optional.
 * If the container is empty, returns Optional.empty().
 * If the number of elements is odd, median is the middle element.
 * If even, median is the smaller of the two middle elements.
 * <p>
 * 4. getKthSmallest(int k) - Returns the k-th smallest integer value wrapped in Optional.
 * If k is out of range (k <= 0 or k > size of container), returns Optional.empty().
 * <p>
 * Constraints:
 * - The container may contain duplicate integers.
 * - All operations must be efficient, ideally aiming for O(log n) for add/delete,
 * and O(n) or better for median and kth smallest retrievals.
 * <p>
 * Example usage:
 * <p>
 * IntegerContainer container = new IntegerContainerImpl();
 * container.add(5);         // returns 1
 * container.add(3);         // returns 2
 * container.add(5);         // returns 3
 * container.getMedian();    // returns Optional.of(5)
 * // (sorted list: [3,5,5], median is the middle 5)
 * container.getKthSmallest(2); // returns Optional.of(5)
 * container.delete(5);      // returns true, container now has [3,5]
 * container.getMedian();    // returns Optional.of(3)
 * // (sorted list: [3,5], median is smaller of two middles -> 3)
 * container.delete(7);      // returns false, 7 not in container
 */


interface IntegerContainer {
    int add(int value);

    boolean delete(int value);

    Optional<Integer> getMedian();

    Optional<Integer> getKthSmallest(int k);
}

public class IntegerContainerImpl implements IntegerContainer {

    private final Map<Integer, Integer> freqMap;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private int size;


    public IntegerContainerImpl() {
        freqMap = new TreeMap<>();
        size = 0;
    }

    @Override
    public int add(int value) {

        readWriteLock.writeLock().lock();
        try {
            freqMap.put(value, freqMap.getOrDefault(value, 0) + 1);
            ++size;
            return size;
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public boolean delete(int value) {

        readWriteLock.writeLock().lock();

        try {
            if (!freqMap.containsKey(value)) {
                return false;
            }

            --size;

            if (freqMap.get(value) == 1) {
                freqMap.remove(value);
                return true;
            }

            freqMap.put(value, freqMap.get(value) - 1);


            return true;

        } finally {
            readWriteLock.writeLock().unlock();
        }


    }

    @Override
    public Optional<Integer> getMedian() {

        readWriteLock.readLock().lock();

        try {

            if (size == 0) {
                return Optional.empty();
            }

            int medianIndex = (size + 1) / 2;

            int i = 0;

            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                i += entry.getValue();

                if (i >= medianIndex) {
                    return Optional.of(entry.getKey());
                }
            }
            return Optional.empty();

        } finally {
            readWriteLock.readLock().unlock();
        }


    }

    @Override
    public Optional<Integer> getKthSmallest(int k) {

        readWriteLock.readLock().lock();

        try {

            if (size == 0) {
                return Optional.empty();
            }

            if (k <= 0 || k > size) {
                return Optional.empty();
            }

            int countSoFar = 0;

            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                countSoFar += entry.getValue();

                if (countSoFar >= k) {
                    return Optional.of(entry.getKey());
                }
            }

            return Optional.empty();

        } finally {
            readWriteLock.readLock().unlock();
        }

    }
}

