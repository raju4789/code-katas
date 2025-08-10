package com.raju.codekatas.coding;

public class KthMissingPositiveNumber {
    public static void main(String[] args) {
        KthMissingPositiveNumber kthMissingPositiveNumber = new KthMissingPositiveNumber();
        int[] arr = {2, 3, 4, 7, 11};
        int k = 5;
        int result = kthMissingPositiveNumber.findKthPositive(arr, k);
        System.out.println("The " + k + "th missing positive number is: " + result);
    }

    public int findKthPositive(int[] arr, int k) {


        if (arr[0] != 1) {
            int noOfMissingNumbers = arr[0] - 1;

            if (noOfMissingNumbers >= k) {
                return k;
            } else {
                k = k - noOfMissingNumbers;
            }
        }

        System.out.println("k1 =" + k);

        for (int i = 0; i < arr.length - 1; ++i) {
            int noOfMissingNumbers = arr[i + 1] - arr[i] - 1;

            if (noOfMissingNumbers != 0) {
                for (int j = arr[i] + 1; j < arr[i + 1]; ++j) {
                    --k;
                    System.out.println("k2 =" + k);
                    if (k == 0) {
                        return j;
                    }
                }
            }
        }

        System.out.println("k3 =" + k);


        return arr[arr.length - 1] + k;


    }
}
