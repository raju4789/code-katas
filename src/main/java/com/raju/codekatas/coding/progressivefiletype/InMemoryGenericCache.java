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
 * This class implements a thread-safe, generic in-memory cache with support for:
 * - Capacity limit with LRU eviction policy
 * - Thread safety using ReadWriteLock
 * - Optional TTL (time-to-live) for each cache entry, with scheduled cleanup of expired entries
 * <p>
 * Uses a doubly linked list + HashMap to maintain LRU order efficiently.
 */
public class InMemoryGenericCache<K, V> {

    // Map to hold key to value + metadata (including TTL info)
    private final Map<K, ValueInfo<V>> valueStore;

    // Map to hold key to linked list node (for LRU order maintenance)
    private final Map<K, Node<K, V>> nodeStore;

    // Maximum capacity of cache
    private final int capacity;

    // Dummy head and tail nodes of doubly linked list (for easy insert/remove)
    private final Node<K, V> head;
    private final Node<K, V> tail;

    // ReadWriteLock for fine-grained thread safety:
    // Multiple readers allowed, writers exclusive
    private final ReentrantReadWriteLock rwLock;

    // Scheduled executor to clean expired cache entries periodically
    private final ScheduledExecutorService cleaner;

    /**
     * Constructor initializes the cache with a fixed capacity.
     * Sets up dummy head and tail nodes for the linked list.
     * Starts a scheduled cleanup task to run every 5 seconds.
     */
    public InMemoryGenericCache(int capacity) {
        this.valueStore = new HashMap<>(capacity);
        this.nodeStore = new HashMap<>();
        this.capacity = capacity;

        // Initialize dummy head and tail nodes for doubly linked list
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);

        // Link head <-> tail
        this.head.setNext(this.tail);
        this.tail.setPrev(this.head);

        // Initialize read-write lock
        this.rwLock = new ReentrantReadWriteLock();

        // Start scheduled task to clean expired entries every 5 seconds
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::cleanExpiredEntries, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Periodic cleanup method to remove expired entries.
     * Acquires write lock as it modifies the cache.
     */
    private void cleanExpiredEntries() {
        long now = System.currentTimeMillis();

        rwLock.writeLock().lock();
        try {
            // Iterate over entries to find expired values
            Iterator<Map.Entry<K, ValueInfo<V>>> it = valueStore.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<K, ValueInfo<V>> entry = it.next();
                ValueInfo<V> valueInfo = entry.getValue();

                // Remove entry if TTL expired
                if (valueInfo.expiresAt != null && valueInfo.expiresAt <= now) {
                    K key = entry.getKey();
                    it.remove();                      // remove from valueStore
                    Node<K, V> node = nodeStore.remove(key); // remove from nodeStore
                    removeNode(node);                 // remove from linked list
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Put or update an entry in the cache without TTL.
     * Moves entry to front (most recently used).
     * Evicts least recently used entry if capacity exceeded.
     */
    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            ValueInfo<V> valueInfo;
            Node<K, V> valueNode;

            if (valueStore.containsKey(key)) {
                // Key already present, update value and move node to front
                valueInfo = valueStore.get(key);
                valueInfo.setValue(value);
                valueStore.put(key, valueInfo);

                valueNode = nodeStore.get(key);

                moveToFront(valueNode);

            } else {
                // New key, check capacity and evict if needed
                if (size() == this.capacity) {
                    // Remove least recently used entry from end
                    K lruKey = tail.getPrev().getKey();
                    valueStore.remove(lruKey);
                    nodeStore.remove(lruKey);
                    removeNode(tail.getPrev());
                }

                // Insert new value and node at front
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

    /**
     * Put or update an entry in the cache with TTL.
     * Similar to put() but sets expiry time.
     */
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

    /**
     * Get the value for the key if present and not expired.
     * Moves the node to front to mark it as recently used.
     * Returns null if key not present or expired.
     */
    public V get(K key) {
        rwLock.readLock().lock();
        try {
            if (!valueStore.containsKey(key)) {
                return null;
            }

            ValueInfo<V> valueInfo = valueStore.get(key);

            // Check expiry: if no expiry or not expired, return value
            if (valueInfo.expiresAt == null || valueInfo.expiresAt > System.currentTimeMillis()) {
                Node<K, V> valueNode = nodeStore.get(key);

                // Move accessed node to front (LRU update)
                moveToFront(valueNode);

                return valueInfo.getValue();
            }

            // Entry expired: return null (consider removing in cleanup thread)
            return null;

        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Remove an entry from cache.
     * Removes from maps and linked list.
     */
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

    /**
     * Returns current number of entries in cache.
     * Uses read lock as size is a read operation.
     */
    public int size() {
        rwLock.readLock().lock();
        try {
            return valueStore.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Clears the cache completely.
     * Removes all entries and resets linked list pointers.
     */
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

    /**
     * Shutdown the cleaner thread gracefully.
     */
    public void shutdown() {
        cleaner.shutdownNow();
    }

    /**
     * Helper method: move a node to the front of the linked list
     * to mark it as most recently used.
     */
    private void moveToFront(Node<K, V> node) {
        if (node == null) return;

        removeNode(node);
        addToFront(node);
    }

    /**
     * Helper method: add a node right after head (front of list).
     */
    private void addToFront(Node<K, V> node) {
        if (node == null) return;

        node.setNext(head.getNext());
        head.getNext().setPrev(node);
        node.setPrev(head);
        head.setNext(node);
    }

    /**
     * Helper method: remove a node from the linked list.
     */
    private void removeNode(Node<K, V> node) {
        if (node == null || node.getPrev() == null || node.getNext() == null) return;

        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    /**
     * Node class representing an entry in the doubly linked list.
     * Stores key, value, and links to prev and next nodes.
     */
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

    /**
     * ValueInfo class wraps the value with optional TTL metadata.
     * Stores expiry time if TTL is specified.
     */
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

/**
 * A simpler alternative implementation of LRU Cache
 * using Java's LinkedHashMap with access-order enabled.
 * Also thread-safe using ReadWriteLock.
 */
class InMemoryGenericCacheLinkedHashMap<K, V> {
    private final Map<K, V> store;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public InMemoryGenericCacheLinkedHashMap(int capacity) {
        this.store = new LinkedHashMap<>(capacity, 0.75f, true) {
            // This method triggers removal of eldest entry when size exceeds capacity
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    /**
     * Insert or update entry in cache.
     * Write lock is used for thread safety.
     */
    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            store.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Get entry from cache.
     * Read lock used for thread safety.
     */
    public V get(K key) {
        rwLock.readLock().lock();
        try {
            return store.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Remove entry from cache.
     * Write lock used for thread safety.
     */
    public void remove(K key) {
        rwLock.writeLock().lock();
        try {
            store.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Return current cache size.
     * Read lock used for thread safety.
     */
    public int size() {
        rwLock.readLock().lock();
        try {
            return store.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Clear all entries in cache.
     * Write lock used for thread safety.
     */
    public void clear() {
        rwLock.writeLock().lock();
        try {
            store.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
