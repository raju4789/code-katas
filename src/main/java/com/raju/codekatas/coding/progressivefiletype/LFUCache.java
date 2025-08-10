package com.raju.codekatas.coding.progressivefiletype;

import java.util.HashMap;
import java.util.Map;

/**
 * Design and implement a data structure for a Least Frequently Used (LFU) cache.
 * <p>
 * Implement the LFUCache class:
 * <p>
 * LFUCache(int capacity) Initializes the object with the capacity of the data structure.
 * <p>
 * int get(int key) Gets the value of the key if the key exists in the cache. Otherwise, returns -1.
 * <p>
 * void put(int key, int value) Update the value of the key if present, or inserts the key if not already present.
 * When the cache reaches its capacity, it should invalidate and remove the least frequently used key before inserting a new item.
 * <p>
 * For this problem, when there is a tie (i.e., two or more keys with the same frequency), the least recently used key would be invalidated.
 * <p>
 * To determine the least frequently used key, a use counter is maintained for each key in the cache.
 * The key with the smallest use counter is the least frequently used key.
 * <p>
 * When a key is first inserted into the cache, its use counter is set to 1 (due to the put operation).
 * The use counter for a key in the cache is incremented either a get or put operation is called on it.
 * <p>
 * The functions get and put must each run in O(1) average time complexity.
 * <p>
 * Example 1:
 * <p>
 * Input
 * ["LFUCache", "put", "put", "get", "put", "get", "get", "put", "get", "get", "get"]
 * [[2], [1, 1], [2, 2], [1], [3, 3], [2], [3], [4, 4], [1], [3], [4]]
 * <p>
 * Output
 * [null, null, null, 1, null, -1, 3, null, -1, 3, 4]
 * <p>
 * Explanation
 * // cnt(x) = the use counter for key x
 * // cache=[] will show the last used order for tiebreakers (leftmost element is most recent)
 * LFUCache lfu = new LFUCache(2);
 * lfu.put(1, 1);   // cache=[1,_], cnt(1)=1
 * lfu.put(2, 2);   // cache=[2,1], cnt(2)=1, cnt(1)=1
 * lfu.get(1);      // return 1
 * // cache=[1,2], cnt(2)=1, cnt(1)=2
 * lfu.put(3, 3);   // 2 is the LFU key because cnt(2)=1 is the smallest, invalidate 2.
 * // cache=[3,1], cnt(3)=1, cnt(1)=2
 * lfu.get(2);      // return -1 (not found)
 * lfu.get(3);      // return 3
 * // cache=[3,1], cnt(3)=2, cnt(1)=2
 * lfu.put(4, 4);   // Both 1 and 3 have the same cnt, but 1 is LRU, invalidate 1.
 * // cache=[4,3], cnt(4)=1, cnt(3)=2
 * lfu.get(1);      // return -1 (not found)
 * lfu.get(3);      // return 3
 * // cache=[3,4], cnt(4)=1, cnt(3)=3
 * lfu.get(4);      // return 4
 * // cache=[4,3], cnt(4)=2, cnt(3)=3
 * <p>
 * Constraints:
 * 1 <= capacity <= 10^4
 * 0 <= key <= 10^5
 * 0 <= value <= 10^9
 * At most 2 * 10^5 calls will be made to get and put.
 */


class NodeFreq {
    int key;
    int value;
    int freq;
    Node prev;
    Node next;

    public NodeFreq(int key, int value) {
        this.key = key;
        this.value = value;
        this.freq = 1;
        this.prev = null;
        this.next = null;
    }
}

public class LFUCache {

    private final int capacity;
    private final Map<Integer, NodeFreq> cache;
    private int size;
    private NodeFreq head;
    private NodeFreq tail;


    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new NodeFreq(-1, -1);
        this.tail = new NodeFreq(-1, -1);
    }

    /**
     * Return the value of the key if it exists in the cache.
     * Otherwise, return -1.
     * Accessing the key should increase its usage frequency.
     */
    public int get(int key) {
        // TODO: Implement this
        return -1;
    }

    /**
     * Insert or update the value for the given key.
     * If capacity is exceeded, evict the least frequently used key.
     * If there is a tie in frequency, evict the least recently used key.
     */
    public void put(int key, int value) {

        if (cache.containsKey(key)) {
            NodeFreq keyNode = cache.get(key);
            keyNode.value = value;
            keyNode.freq += 1;


        }
    }
}

