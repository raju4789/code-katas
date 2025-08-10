package com.raju.codekatas.coding;

/**
 * Given two strings s and goal, return true if you can swap two letters in s so the result is equal to goal, otherwise, return false.
 * <p>
 * Swapping letters is defined as taking two indices i and j (0-indexed) such that i != j and swapping the characters at s[i] and s[j].
 * <p>
 * For example, swapping at indices 0 and 2 in "abcd" results in "cbad".
 * <p>
 * <p>
 * Example 1:
 * <p>
 * Input: s = "ab", goal = "ba"
 * Output: true
 * Explanation: You can swap s[0] = 'a' and s[1] = 'b' to get "ba", which is equal to goal.
 * Example 2:
 * <p>
 * Input: s = "ab", goal = "ab"
 * Output: false
 * Explanation: The only letters you can swap are s[0] = 'a' and s[1] = 'b', which results in "ba" != goal.
 * Example 3:
 * <p>
 * Input: s = "aa", goal = "aa"
 * Output: true
 * Explanation: You can swap s[0] = 'a' and s[1] = 'a' to get "aa", which is equal to goal.
 * <p>
 * <p>
 * Constraints:
 * <p>
 * 1 <= s.length, goal.length <= 2 * 104
 * s and goal consist of lowercase letters.
 */

public class BuddyStrings {


    public static void main(String[] args) {
        BuddyStrings buddyStrings = new BuddyStrings();
        String s = "abcaa";
        String goal = "abcbb";
        boolean result = buddyStrings.buddyStrings(s, goal);
        System.out.println("Can swap to make equal: " + result); // Should print true
    }

    public boolean buddyStrings(String s, String goal) {

        if (s.length() != goal.length()) {
            return false;
        }

        if (s.equals(goal)) {
            int[] charCount = new int[26];
            char[] sChars = s.toCharArray();

            for (char c : sChars) {
                charCount[c - 'a'] += 1;

                if (charCount[c - 'a'] > 1) {
                    return true;
                }
            }
            return false;

        }

        char[] sChars = s.toCharArray();
        char[] goalChars = goal.toCharArray();

        char[] sdiff = new char[s.length()];
        char[] goalDiff = new char[goal.length()];

        int diffCount = 0;
        int diffIndex = 0;

        for (int i = 0; i < sChars.length; ++i) {

            if (sChars[i] != goalChars[i]) { // b != a
                sdiff[diffIndex] = sChars[i];      //[a b ]
                goalDiff[diffIndex] = goalChars[i];
                ++diffIndex;// [b a]
                ++diffCount; // 2
            }

            if (diffCount > 2) {
                break;
            }

        }

        if (diffCount == 2) {
            return (sdiff[0] == goalDiff[1]) && (sdiff[1] == goalDiff[0]);
        }

        return false;


    }

}
