package com.raju.codekatas.coding;

import java.util.ArrayList;
import java.util.List;

/**
 * You are given a nested list of integers nestedList. Each element is either an integer or a list whose elements may also be integers or other lists.
 * <p>
 * The depth of an integer is the number of lists that it is inside of. For example, the nested list [1,[2,2],[[3],2],1] has each integer's value set to its depth. Let maxDepth be the maximum depth of any integer.
 * <p>
 * The weight of an integer is maxDepth - (the depth of the integer) + 1.
 * <p>
 * Return the sum of each integer in nestedList multiplied by its weight.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * <p>
 * <p>
 * Input: nestedList = [[1,1],2,[1,1]]
 * Output: 8
 * Explanation: Four 1's with a weight of 1, one 2 with a weight of 2.
 * 1*1 + 1*1 + 2*2 + 1*1 + 1*1 = 8
 * Example 2:
 * <p>
 * <p>
 * Input: nestedList = [1,[4,[6]]]
 * Output: 17
 * Explanation: One 1 at depth 3, one 4 at depth 2, and one 6 at depth 1.
 * 1*3 + 4*2 + 6*1 = 17
 * <p>
 * <p>
 * Constraints:
 * <p>
 * 1 <= nestedList.length <= 50
 * The values of the integers in the nested list is in the range [-100, 100].
 * The maximum depth of any integer is less than or equal to 50.
 * There are no empty lists.
 */

public interface NestedInteger {
//    // Constructor initializes an empty nested list.
//    public NestedInteger();
//
//    // Constructor initializes a single integer.
//    public NestedInteger(int value);

    // @return true if this NestedInteger holds a single integer, rather than a nested list.
    public boolean isInteger();

    // @return the single integer that this NestedInteger holds, if it holds a single integer
    // Return null if this NestedInteger holds a nested list
    public Integer getInteger();

    // Set this NestedInteger to hold a single integer.
    public void setInteger(int value);

    // Set this NestedInteger to hold a nested list and adds a nested integer to it.
    public void add(NestedInteger ni);

    // @return the nested list that this NestedInteger holds, if it holds a nested list
    // Return empty list if this NestedInteger holds a single integer
    public List<NestedInteger> getList();
}


class NestedIntegerImpl implements NestedInteger {
    private Integer singleInteger;
    private List<NestedInteger> list;

    // Default constructor initializes an empty list
    public NestedIntegerImpl() {
        this.list = new ArrayList<>();
    }

    // Constructor to initialize a single integer
    public NestedIntegerImpl(int value) {
        this.singleInteger = value;
    }

    @Override
    public boolean isInteger() {
        return singleInteger != null;
    }

    @Override
    public Integer getInteger() {
        return singleInteger;
    }

    @Override
    public void setInteger(int value) {
        this.singleInteger = value;
        this.list = null; // Clear list since it is now an integer
    }

    @Override
    public void add(NestedInteger ni) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.add(ni);
        this.singleInteger = null; // Clear integer since it is now a list
    }

    @Override
    public List<NestedInteger> getList() {
        if (this.list == null) {
            return new ArrayList<>();
        }
        return this.list;
    }
}


class NestedIntegerSum {

    public static void main(String[] args) {
        NestedIntegerSum solution = new NestedIntegerSum();

        // Example 1: [[1,1], 2, [1,1]]
        List<NestedInteger> example1 = new ArrayList<>();

        NestedIntegerImpl list1 = new NestedIntegerImpl();
        list1.add(new NestedIntegerImpl(1));
        list1.add(new NestedIntegerImpl(1));

        NestedIntegerImpl list2 = new NestedIntegerImpl();
        list2.add(new NestedIntegerImpl(1));
        list2.add(new NestedIntegerImpl(1));

        example1.add(list1);
        example1.add(new NestedIntegerImpl(2));
        example1.add(list2);

        int result1 = solution.depthSumInverse(example1);
        System.out.println("Example 1 Output: " + result1);  // Expected: 8

        // Example 2: [1, [4, [6]]]
        List<NestedInteger> example2 = new ArrayList<>();

        NestedIntegerImpl nested4 = new NestedIntegerImpl();
        NestedIntegerImpl nested6 = new NestedIntegerImpl();
        nested6.add(new NestedIntegerImpl(6));

        nested4.add(new NestedIntegerImpl(4));
        nested4.add(nested6);

        example2.add(new NestedIntegerImpl(1));
        example2.add(nested4);

        int result2 = solution.depthSumInverse(example2);
        System.out.println("Example 2 Output: " + result2);  // Expected: 17
    }

    public int depthSumInverse(List<NestedInteger> nestedList) {

        int maxDepth = getMaxDepth(nestedList);
        int depth = 1;

        return calculateDepthSumInverse(nestedList, maxDepth, depth);
    }

    public int calculateDepthSumInverse(List<NestedInteger> nestedList, int maxDepth, int depth) {
        int sum = 0;
        for (NestedInteger ni : nestedList) {
            if (ni.isInteger()) {
                sum += ni.getInteger() * (maxDepth - depth + 1);
            }
            sum += calculateDepthSumInverse(ni.getList(), maxDepth, depth + 1);

        }

        return sum;
    }

    public int getMaxDepth(List<NestedInteger> nestedList) {

        return getMaxDepth(nestedList, 1);

    }

    private int getMaxDepth(List<NestedInteger> nestedList, int depth) {

        for (NestedInteger ni : nestedList) {
            if (!ni.isInteger()) {
                return Math.max(depth, getMaxDepth(ni.getList(), depth + 1));
            }
        }

        return depth;
    }
}
