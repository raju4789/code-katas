package com.raju.codekatas.coding.progressivefiletype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 🚀 Problem Statement: Generic In-Memory Cache (Revolut-style)
 * <p>
 * Build a thread-safe, in-memory generic cache in Java. Your solution should be production-grade,
 * testable, and extensible, with clear stages of increasing complexity.
 * <p>
 * ✳️ Functional Requirements:
 * <p>
 * Stage 1: Basic Functionality
 * - Implement put(K key, V value), get(K key): V, and remove(K key)
 * - Internally store the entries using an efficient data structure
 * <p>
 * Stage 2: Capacity Limit
 * - Support max cache size (passed via constructor)
 * - When capacity is exceeded, evict the oldest (FIFO or LRU in later stages)
 * <p>
 * Stage 3: Thread Safety
 * - Ensure safe access under concurrent reads and writes
 * - Avoid race conditions and data corruption
 * <p>
 * Stage 4: LRU Eviction (Stretch)
 * - Evict least recently used entry upon overflow
 * - Accessing an item should update its usage order
 * <p>
 * Stage 5: TTL Support (Bonus)
 * - Support Time-To-Live (expiry per key)
 * - Expired keys should be removed automatically or ignored on access
 * <p>
 * ✳️ Constraints:
 * - Use only core Java (no frameworks or caching libraries)
 * - Follow SOLID principles and clean code practices
 * - Use generics: class Cache<K, V>
 * <p>
 * ✳️ Example Usage:
 * <p>
 * Cache<String, String> cache = new InMemoryCache<>(2);
 * cache.put("a", "alpha");
 * cache.put("b", "beta");
 * cache.get("a");               // returns "alpha"
 * cache.put("c", "gamma");      // if LRU, evicts "b"
 * cache.get("b");               // returns null
 * <p>
 * ✳️ Threading Example:
 * // Multiple threads should be able to read/write safely
 * <p>
 * Thread t1 = new Thread(() -> cache.put("x", "1"));
 * Thread t2 = new Thread(() -> cache.get("x"));
 * t1.start(); t2.start();
 * <p>
 * 🔍 Discussion Points:
 * - Difference between coarse vs fine-grained locks
 * - Use of LinkedHashMap for LRU
 * - Trade-offs of TTL and cleanup strategies
 */

public class InMemoryGenericCache<K, V> {

    private final Map<K, ValueInfo<V>> valueStore;
    private final Map<K, Node<K, V>> nodeStore;
    private final int capacity;
    private final Node<K, V> head;
    private final Node<K, V> tail;

    private final ReentrantReadWriteLock rwLock;

    private final ScheduledExecutorService cleaner;


    public InMemoryGenericCache(int capacity) {
        this.valueStore = new HashMap<>(capacity);
        this.nodeStore = new HashMap<>();
        this.capacity = capacity;
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);

        this.head.setNext(this.tail);
        this.tail.setPrev(this.head);

        this.rwLock = new ReentrantReadWriteLock();

        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::cleanExpiredEntries, 5, 5, TimeUnit.SECONDS);
    }

    private void cleanExpiredEntries() {
        long now = System.currentTimeMillis();

        rwLock.writeLock().lock();

        try {
            Iterator<Map.Entry<K, ValueInfo<V>>> it = valueStore.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<K, ValueInfo<V>> entry = it.next();
                ValueInfo<V> valueInfo = entry.getValue();
                if (valueInfo.expiresAt != null && valueInfo.expiresAt <= now) {
                    K key = entry.getKey();
                    it.remove();
                    Node<K, V> node = nodeStore.remove(key);
                    removeNode(node);
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void put(K key, V value) {

        rwLock.writeLock().lock();

        try {
            ValueInfo<V> valueInfo;
            Node<K, V> valueNode;
            if (valueStore.containsKey(key)) {
                valueInfo = valueStore.get(key);
                valueInfo.setValue(value);
                valueStore.put(key, valueInfo);

                valueNode = nodeStore.get(key);

                moveToFront(valueNode);
            } else {

                if (size() == this.capacity) {
                    K lruKey = tail.getPrev().getKey();
                    valueStore.remove(lruKey);
                    nodeStore.remove(lruKey);
                    removeNode(tail.getPrev());
                }
                valueInfo = new ValueInfo<>(value);
                valueStore.put(key, valueInfo);

                valueNode = new Node<>(key, value);

                nodeStore.put(key, valueNode);

                addToFront(valueNode);
            }
        } finally {
            rwLock.writeLock().unlock();
        }


    }

    public void put(K key, V value, Long ttl) {
        rwLock.writeLock().lock();

        try {
            ValueInfo<V> valueInfo;
            Node<K, V> valueNode;
            if (valueStore.containsKey(key)) {
                valueInfo = valueStore.get(key);
                valueInfo.setValue(value);
                valueInfo.setTtl(ttl);
                valueStore.put(key, valueInfo);

                valueNode = nodeStore.get(key);

                moveToFront(valueNode);
            } else {

                if (size() == this.capacity) {
                    K lruKey = tail.getPrev().getKey();
                    valueStore.remove(lruKey);
                    nodeStore.remove(lruKey);
                    removeNode(tail.getPrev());
                }
                valueInfo = new ValueInfo<>(value, ttl);
                valueStore.put(key, valueInfo);

                valueNode = new Node<>(key, value);

                nodeStore.put(key, valueNode);

                addToFront(valueNode);
            }

        } finally {
            rwLock.writeLock().unlock();
        }


    }

    public V get(K key) {

        rwLock.readLock().lock();

        try {

            if (!valueStore.containsKey(key)) {
                return null;
            }

            ValueInfo<V> valueInfo = valueStore.get(key);

            if (valueInfo.expiresAt == null || valueInfo.expiresAt > System.currentTimeMillis()) {

                Node<K, V> valueNode = nodeStore.get(key);
                moveToFront(valueNode);

                return valueInfo.getValue();
            }

            return null;
        } finally {
            rwLock.readLock().unlock();
        }


    }

    public void remove(K key) {

        rwLock.writeLock().lock();

        try {
            if (!valueStore.containsKey(key)) {
                return;
            }
            valueStore.remove(key);

            removeNode(nodeStore.get(key));

            nodeStore.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    public int size() {
        rwLock.readLock().lock();

        try {
            return valueStore.size();
        } finally {
            rwLock.readLock().unlock();
        }

    }

    public void clear() {

        rwLock.writeLock().lock();

        try {
            valueStore.clear();
            nodeStore.clear();

            this.head.setNext(this.tail);
            this.tail.setPrev(this.head);
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    public void shutdown() {
        cleaner.shutdownNow();
    }

    private void moveToFront(Node<K, V> node) {

        if (node == null) return;

        removeNode(node);
        addToFront(node);
    }

    private void addToFront(Node<K, V> node) {
        if (node == null) return;

        node.setNext(head.getNext());
        head.getNext().setPrev(node);
        node.setPrev(head);
        head.setNext(node);
    }

    private void removeNode(Node<K, V> node) {
        if (node == null || node.getPrev() == null || node.getNext() == null) return;

        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    private static class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> prev;
        private Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.prev = null;
            this.next = null;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<K, V> getPrev() {
            return prev;
        }

        public void setPrev(Node<K, V> prev) {
            this.prev = prev;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }
    }

    private static class ValueInfo<V> {
        private V value;
        private Long ttl;
        private Long expiresAt;

        public ValueInfo(V value) {
            this.value = value;
            this.ttl = null;
            this.expiresAt = null;
        }

        public ValueInfo(V value, Long ttl) {
            this.value = value;
            this.ttl = ttl;
            this.expiresAt = System.currentTimeMillis() + ttl;
        }


        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public void setTtl(Long ttl) {
            this.ttl = ttl;
            this.expiresAt = System.currentTimeMillis() + ttl;
        }

    }
}

class InMemoryGenericCacheLinkedHashMap<K, V> {
    private final Map<K, V> store;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public InMemoryGenericCacheLinkedHashMap(int capacity) {
        this.store = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            store.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public V get(K key) {
        rwLock.readLock().lock();
        try {
            return store.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void remove(K key) {
        rwLock.writeLock().lock();
        try {
            store.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public int size() {
        rwLock.readLock().lock();
        try {
            return store.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void clear() {
        rwLock.writeLock().lock();
        try {
            store.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}

