package com.raju.codekatas.coding.progressivefiletype;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.
 * <p>
 * Implement the LRUCache class:
 * <p>
 * LRUCache(int capacity) Initialize the LRU cache with positive size capacity.
 * int get(int key) Return the value of the key if the key exists, otherwise return -1.
 * void put(int key, int value) Update the value of the key if the key exists. Otherwise, add the key-value pair to the cache.
 * If the number of keys exceeds the capacity from this operation, evict the least recently used key.
 * <p>
 * The functions get and put must each run in O(1) average time complexity.
 * <p>
 * Constraints:
 * 1 <= capacity <= 3000
 * 0 <= key <= 10^4
 * 0 <= value <= 10^5
 * At most 2 * 10^5 calls will be made to get and put.
 */

class Node {


    int key;
    int value;
    Node prev;
    Node next;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}

/**
 * LRUCache maintains a fixed-capacity cache of key-value pairs.
 * The least recently used (LRU) entry is evicted first when the capacity is exceeded.
 * Both get() and put() operations run in average O(1) time.
 */
public class LRUCache {

    // Maps keys to their corresponding node in the doubly linked list
    private final Map<Integer, Node> cache;

    // Fixed maximum number of items the cache can hold
    private final int capacity;
    // Head and tail pointers for the doubly linked list
    // Most recently used = head; Least recently used = tail
    private final Node head;
    private final Node tail;
    // Current size of the cache
    private int size;

    public LRUCache(int capacity) {
        this.cache = new HashMap<>(capacity);
        this.capacity = capacity;
        this.size = 0;
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);

        head.setNext(tail);
        tail.setPrev(head);
    }

    /**
     * Retrieves the value for the given key if it exists in the cache.
     * Also moves the accessed node to the front (most recently used).
     */
    public int get(int key) {
        if (!cache.containsKey(key))
            return -1;

        Node node = cache.get(key);
        moveToFront(node); // Mark as most recently used
        return node.getValue();
    }

    /**
     * Inserts or updates the value for the given key.
     * If inserting causes the cache to exceed capacity, evicts the LRU (tail).
     */
    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node keyNode = cache.get(key);
            keyNode.setValue(value);
            moveToFront(keyNode);
            // No need to explicity add and just value changes node still exists
            //cache.put(key, keyNode);
            return;
        }

        Node newNode = new Node(key, value);

        if (size == capacity) {
            cache.remove(tail.getPrev().getKey());
            removeNode(tail.getPrev());
            --size;
        }

        addToFront(newNode);

        cache.put(key, newNode);
        ++size;
    }

    /**
     * Moves a node to the front of the linked list.
     * No-op if node is already at the front.
     */
    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    /**
     * Removes a node from its current position in the doubly linked list.
     * Handles head, tail, and middle nodes.
     */
    private void removeNode(Node node) {
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    /**
     * Adds a node to the front (head) of the doubly linked list.
     * Also initializes tail if the list was empty.
     */
    private void addToFront(Node node) {

        node.setNext(head.getNext());
        node.getNext().setPrev(node);
        node.setPrev(head);
        head.setNext(node);
    }
}


class LRUCacheLinkedHashMap {
    private final Map<Integer, Integer> cache;

    public LRUCacheLinkedHashMap(int capacity) {
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    // Return the value if key exists; otherwise, return -1
    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    // Insert or update the key-value pair
    public void put(int key, int value) {
        cache.put(key, value);
    }


}
