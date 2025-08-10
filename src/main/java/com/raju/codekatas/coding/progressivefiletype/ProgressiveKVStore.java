package com.raju.codekatas.coding.progressivefiletype;

import java.util.*;

/**
 * Progressive In-Memory Key-Value Store
 * <p>
 * Features:
 * 1. Basic key-value set/get
 * 2. TTL support (key expires after ttl seconds)
 * 3. Point-in-time queries with versioning
 * 4. Deletion via tombstone (soft delete with historical retention)
 */

class Version {
    String value;
    Long expireAt;   // Absolute expiration time (in ms), or null = never expires
    Long timeStamp;  // Logical set time (in ms)

    // Constructor for set(key, value)
    public Version(String value) {
        this.value = value;
        this.timeStamp = System.currentTimeMillis();
        this.expireAt = null; // Never expires
    }

    // Constructor for set(key, value, ttl)
    public Version(String value, Long expireAt) {
        this.value = value;
        this.timeStamp = System.currentTimeMillis();
        this.expireAt = expireAt;
    }

    // Constructor for set(key, value, timestamp, ttl)
    public Version(String value, Long expireAt, Long timeStamp) {
        this.value = value;
        this.expireAt = expireAt;
        this.timeStamp = timeStamp;
    }

    public String getValue() {
        return value;
    }
}

public class ProgressiveKVStore {

    private final Map<String, List<Version>> kvStore;

    public ProgressiveKVStore() {
        kvStore = new HashMap<>();
    }

    /**
     * Stage 1:
     * Sets a new version of a key with no TTL (never expires).
     * If key already exists, appends a new version.
     */
    public void set(String key, String value) {
        Version version = new Version(value);
        kvStore.computeIfAbsent(key, k -> new ArrayList<>()).add(version);
    }

    /**
     * Stage 2:
     * Sets a version of the key with a TTL (expires after ttl seconds).
     * New version overrides any older versions in future get().
     */
    public void set(String key, String value, int ttl) {
        long now = System.currentTimeMillis();
        long expireAt = now + ttl * 1000L;
        Version version = new Version(value, expireAt);
        kvStore.computeIfAbsent(key, k -> new ArrayList<>()).add(version);
    }

    /**
     * Stage 3:
     * Sets a version of the key at a given timestamp with TTL.
     * Supports building historical timelines.
     */
    public void set(String key, String value, long timestamp, int ttl) {
        long expireAt = timestamp + ttl * 1000L;
        Version version = new Version(value, expireAt, timestamp);
        kvStore.computeIfAbsent(key, k -> new ArrayList<>()).add(version);
    }

    /**
     * Stage 1 & 2:
     * Returns the latest value for a key at current time.
     * Skips expired versions and tombstones.
     */
    public String get(String key) {
        List<Version> versions = kvStore.get(key);
        if (versions == null || versions.isEmpty()) return null;

        long now = System.currentTimeMillis();

        return versions.stream()
                .filter(v -> v.timeStamp <= now && (v.expireAt == null || v.expireAt > now))
                .max(Comparator.comparingLong(v -> v.timeStamp))
                .map(Version::getValue)
                .orElse(null);
    }

    /**
     * Stage 3:
     * Returns the value that was valid at the given timestamp.
     * Ignores versions set after the given timestamp or expired at that time.
     */
    public String get(String key, long timestamp) {
        List<Version> versions = kvStore.get(key);
        if (versions == null || versions.isEmpty()) return null;

        return versions.stream()
                .filter(v -> v.timeStamp <= timestamp && (v.expireAt == null || v.expireAt > timestamp))
                .max(Comparator.comparingLong(v -> v.timeStamp))
                .map(Version::getValue)
                .orElse(null);
    }

    /**
     * Stage 4:
     * Marks the key as deleted using a tombstone (null value version).
     * Tombstone disables `get(key)` from returning any values set before this time.
     * Point-in-time queries still work for timestamps before deletion.
     */
    public void delete(String key) {
        long now = System.currentTimeMillis();
        Version tombstone = new Version(null, null, now); // no TTL, null value
        kvStore.computeIfAbsent(key, k -> new ArrayList<>()).add(tombstone);
    }

    /**
     * Utility method: Print all versions (for testing/debugging)
     */
    public void printVersions(String key) {
        List<Version> versions = kvStore.get(key);
        if (versions == null || versions.isEmpty()) {
            System.out.println("No versions for key: " + key);
            return;
        }

        System.out.println("Versions for key [" + key + "]:");
        for (Version v : versions) {
            String value = v.value == null ? "TOMBSTONE" : v.value;
            String expiry = v.expireAt == null ? "never" : String.valueOf(v.expireAt);
            System.out.println("  value=" + value + ", timeStamp=" + v.timeStamp + ", expireAt=" + expiry);
        }
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException {
        ProgressiveKVStore store = new ProgressiveKVStore();

        // Stage 1: Simple set and get
        store.set("user", "Alice");
        System.out.println(store.get("user")); // Alice

        // Stage 2: Set with TTL (2 seconds)
        store.set("session", "token123", 2);
        System.out.println(store.get("session")); // token123
        Thread.sleep(2500); // wait for TTL to expire
        System.out.println(store.get("session")); // null (expired)

        // Stage 3: Set with custom timestamp and TTL
        long now = System.currentTimeMillis();
        store.set("config", "v1", now - 10_000, 20); // valid from now-10s to now+10s
        store.set("config", "v2", now + 1000, 20);   // future version

        System.out.println(store.get("config"));                // v1
        System.out.println(store.get("config", now - 1000));    // v1
        System.out.println(store.get("config", now + 2000));    // v2

        // Stage 4: Delete key
        store.delete("user");
        System.out.println(store.get("user")); // null (deleted)
        store.set("user", "Bob");
        System.out.println(store.get("user")); // Bob (new value after delete)

        store.printVersions("user");
    }
}

