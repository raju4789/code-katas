package com.raju.codekatas.coding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://leetcode.com/problems/contains-duplicate-ii/description/">...</a>
 * Given an integer array nums and an integer k, return true if there are two distinct indices i and j in the array such that nums[i] == nums[j] and abs(i - j) <= k.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,2,3,1], k = 3
 * Output: true
 * Example 2:
 * <p>
 * Input: nums = [1,0,1,1], k = 1
 * Output: true
 * Example 3:
 * <p>
 * Input: nums = [1,2,3,1,2,3], k = 2
 * Output: false
 * <p>
 * <p>
 * Constraints:
 * <p>
 * 1 <= nums.length <= 105
 * -109 <= nums[i] <= 109
 * 0 <= k <= 105
 */

public class ContainsDuplicateII {

    public boolean containsNearbyDuplicate(int[] nums, int k) {

        Map<Integer, Integer> numToIdx = new HashMap<>();

        for (int i = 0; i < nums.length; ++i) {

            if (numToIdx.containsKey(nums[i])) {
                int prevIdx = numToIdx.get(nums[i]);

                if (Math.abs(i - prevIdx) <= k) {
                    return true;
                }
            }

            numToIdx.put(nums[i], i);
        }

        return false;

    }

    public boolean containsNearbyDuplicate1(int[] nums, int k) {

        Map<Integer, List<Integer>> numToIdx = new HashMap<>();

        for (int i = 0; i < nums.length; ++i) {
            if (numToIdx.containsKey(nums[i])) {
                List<Integer> indicesOfI = numToIdx.get(nums[i]);

                for (Integer idx : indicesOfI) {
                    if (Math.abs(idx - i) <= k) {
                        return true;
                    }
                }

                numToIdx.get(nums[i]).add(i);
                numToIdx.put(nums[i], numToIdx.get(nums[i]));

            } else {
                List<Integer> indicesOfI = new ArrayList<>();
                indicesOfI.add(i);
                numToIdx.put(nums[i], indicesOfI);
            }
        }

        return false;

    }
}
