package com.raju.codekatas.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * You are given a sorted integer array nums where elements are in ascending order, and two integers lower and upper representing a closed interval [lower, upper].
 * You need to find the smallest sorted list of ranges that cover every number in this interval [lower, upper] exactly once, but excluding the numbers present in nums.
 * Each range should be in the format:
 * "a" if it’s a single number
 * "a->b" if it’s a range from a to b (inclusive)
 * Return the list of ranges as strings.
 * <p>
 * 🔎 Example 1
 * Input: nums = [0, 1, 3, 50, 75], lower = 0, upper = 99
 * Output: ["2", "4->49", "51->74", "76->99"]
 * Explanation:
 * - Numbers present in nums: 0, 1, 3, 50, 75
 * - Missing ranges: [2], [4 to 49], [51 to 74], [76 to 99]
 * <p>
 * 🔎 Example 2
 * Input: nums = [], lower = 1, upper = 1
 * Output: ["1"]
 * Explanation: Whole range is missing.
 * <p>
 * 🔎 Example 3
 * Input: nums = [], lower = -3, upper = -1
 * Output: ["-3->-1"]
 * <p>
 * ✅ Constraints
 * 0 <= nums.length <= 100
 * -2^31 <= nums[i] <= 2^31 - 1
 * lower <= upper
 * All values of nums are unique and sorted.
 */
public class MissingRanges {
    public static void main(String[] args) {
        MissingRanges missingRanges = new MissingRanges();
        int[] nums = {0, 1, 3, 50, 75};
        int lower = 0;
        int upper = 99;
        System.out.println(Arrays.toString(missingRanges.findMissingRanges(nums, lower, upper)));
    }

    public String[] findMissingRanges(int[] nums, int lower, int upper) {

        if (nums.length == 0) {
            if (lower == upper) {
                return new String[]{String.valueOf(lower)};
            } else {
                String missingRange = lower + "->" + upper;
                return new String[]{missingRange};
            }
        }

        List<String> missingRanges = new ArrayList<>();

        if (nums[0] != lower) {
            if (nums[0] - lower == 1) {
                missingRanges.add(String.valueOf(lower));
            } else {
                String missingRange = lower + "->" + (nums[0] - 1);
                missingRanges.add(missingRange);
            }
        }

        for (int i = 0; i < nums.length - 1; ++i) {

            if (nums[i + 1] - nums[i] == 2) {
                missingRanges.add(String.valueOf(nums[i] + 1));
            } else if (nums[i + 1] - nums[i] > 2) {
                String missingRange = nums[i] + 1 + "->" + (nums[i + 1] - 1);
                missingRanges.add(missingRange);
            }
        }

        if (nums[nums.length - 1] != upper) {
            if (upper - nums[nums.length - 1] == 1) {
                missingRanges.add(String.valueOf(upper));
            } else {
                String missingRange = (nums[nums.length - 1] + 1) + "->" + upper;
                missingRanges.add(missingRange);
            }
        }

        return missingRanges.toArray(new String[0]);
    }

    public String[] findMissingRangesOptimised(int[] nums, int lower, int upper) {
        List<String> missingRanges = new ArrayList<>();

        long prev = (long) (lower - 1);

        for (int i = 0; i <= nums.length; ++i) {
            long current = (i < nums.length) ? nums[i] : (long) (upper + 1);

            if (current - prev >= 2) {
                formatRange(prev + 1, current - 1, missingRanges);
            }

            prev = current;
        }

        return missingRanges.toArray(new String[0]);
    }

    private void formatRange(long start, long end, List<String> missingRanges) {
        if (start == end) {
            missingRanges.add(String.valueOf(start));
        } else {
            missingRanges.add(start + "->" + end);
        }
    }
}
