package com.raju.codekatas.coding.progressivefiletype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implement a basic in-memory key-value store
 * 💬 Requirements — Level 1
 * Support the following operations:
 * <p>
 * put(String key, String value): stores the key-value pair.
 * <p>
 * get(String key): returns the value if the key exists, otherwise return null.
 * <p>
 * remove(String key): deletes the key-value pair if present.
 * <p>
 * 💬 Level 2
 * Add a method:
 * <p>
 * getAllKeys(): returns a list of all current keys in the store.
 * <p>
 * 💬 Level 3
 * Support expiration (TTL) for keys.
 * <p>
 * Add a put(String key, String value, int ttlInSeconds) method.
 * <p>
 * When a key expires, get should return null even though the key was originally inserted.
 * <p>
 * You can use System.currentTimeMillis() or System.nanoTime().
 * <p>
 * ✅ Optional Constraints
 * You can assume single-threaded for today (we’ll add concurrency later).
 * <p>
 * Use plain Java collections (e.g., HashMap).
 */


public class KeyValueStore {

    private final Map<String, ValueInfo> kvMap = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    void put(String key, String value) {

        rwLock.writeLock().lock();

        try {
            kvMap.put(key, new ValueInfo(value, null));
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    void put(String key, String value, int ttlInSeconds) {

        rwLock.writeLock().lock();

        try {
            kvMap.put(key, new ValueInfo(value, System.currentTimeMillis() + (ttlInSeconds * 1000L)));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    String get(String key) {

        rwLock.readLock().lock();

        try {

            if (!kvMap.containsKey(key)) {
                return null;
            }

            Long currentTime = System.currentTimeMillis();

            if (kvMap.get(key).expiredAt == null || kvMap.get(key).expiredAt > currentTime) {
                return kvMap.get(key).value;
            }

            return null;

        } finally {
            rwLock.readLock().unlock();
        }
    }

    void remove(String key) {

        rwLock.writeLock().lock();

        try {
            kvMap.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    List<String> getAllKeys() {

        rwLock.readLock().lock();

        try {
            return new ArrayList<>(kvMap.keySet());

        } finally {
            rwLock.readLock().unlock();
        }


    }

    private static class ValueInfo {
        String value;
        Long expiredAt;

        public ValueInfo(String value, Long expiredAt) {
            this.value = value;
            this.expiredAt = expiredAt;
        }
    }


}
