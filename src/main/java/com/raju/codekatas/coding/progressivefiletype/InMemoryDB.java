package com.raju.codekatas.coding.progressivefiletype;

import java.io.Serializable;
import java.util.*;

/**
 * Problem Statement: In-Memory Versioned Key-Field Database with TTL and Snapshot Support
 * <p>
 * You are to implement an in-memory key-field-value database with support for field versioning, TTL (time-to-live),
 * and snapshot-based version control. The design progresses through multiple levels, with each level building upon
 * the previous.
 * <p>
 * --------------------------------------------------------------------
 * LEVEL 1: Basic Key-Field Operations
 * --------------------------------------------------------------------
 * - put(String key, String field, String value)
 * Store the given value under the field for the specified key.
 * Overwrites the previous value if it exists (implicitly using current time).
 * <p>
 * - get(String key, String field): String
 * Retrieve the most recent (non-expired) value stored under the field for the key.
 * <p>
 * - delete(String key, String field)
 * Remove the field from the key entirely.
 * <p>
 * --------------------------------------------------------------------
 * LEVEL 2: Field Scanning Operations
 * --------------------------------------------------------------------
 * - scan(String key): List<String>
 * Return all fields and their most recent (non-expired) values under the key.
 * Format each entry as: <field>(<value>)
 * <p>
 * - scanWithPrefix(String prefix): List<String>
 * Return all fields under the key whose names start with the given prefix.
 * Format each entry as: <field>(<value>)
 * <p>
 * --------------------------------------------------------------------
 * LEVEL 3: Time-Aware Versioning with TTL
 * --------------------------------------------------------------------
 * - Each field can have multiple versions, each version having:
 * - value: String
 * - timestamp: Long (epoch seconds)
 * - optional TTL: Long (in seconds)
 * <p>
 * - put(String key, String field, String value, Long timestamp)
 * Insert a version for the field at the given timestamp, without TTL.
 * <p>
 * - put(String key, String field, String value, Long timestamp, Long ttl)
 * Insert a version with a TTL (in seconds), valid until (timestamp + ttl).
 * <p>
 * - get(String key, String field, Long timestamp): String
 * Retrieve the latest version of the field whose timestamp is ≤ the given timestamp,
 * and which is not expired as of that time.
 * <p>
 * - scan(String key, Long timestamp): List<String>
 * Return all valid (non-expired) fields under the key as of the given timestamp.
 * Format each entry as: <field>(<value>)
 * <p>
 * - scanWithPrefix(String prefix, Long timestamp): List<String>
 * Same as above, but only include fields whose names start with the prefix.
 * <p>
 * --------------------------------------------------------------------
 * LEVEL 4: Snapshot and Version Control
 * --------------------------------------------------------------------
 * - snapshot()
 * Take a snapshot of the current state of the DB.
 * Each snapshot is assigned an incrementing version ID starting from 1.
 * Snapshots are isolated: changes after a snapshot do not affect previous ones.
 * <p>
 * - getSnapShot(int snapShotId)
 * Restore the DB to a previously taken snapshot.
 * All future read/write operations affect this snapshot version.
 * If the snapshot ID is invalid, do nothing.
 * <p>
 * Notes:
 * - Time should be consistently represented in epoch seconds (not milliseconds).
 * - Field values should be considered expired if current timestamp > (field timestamp + ttl).
 * - Only the latest non-expired version ≤ the read timestamp should be returned for a field.
 * - Field versioning is maintained per field using a list of versions.
 * - Snapshots must store a **deep copy** of the current DB state.
 */


public class InMemoryDB {


    private final Map<Integer, IMDBVersion> imdbVersions;
    private int latestVersion;
    private IMDBVersion currentVersion;

    public InMemoryDB() {
        this.imdbVersions = new HashMap<>();
        this.latestVersion = 1;

        this.currentVersion = new IMDBVersion();

        this.imdbVersions.put(latestVersion, this.currentVersion);
    }

    public void put(String key, String field, String value) {
        ValueInfo valueInfo = currentVersion.keyInfo.getOrDefault(key, new ValueInfo());

        List<FieldValue> fieldValues = valueInfo.fieldInfo.getOrDefault(field, new ArrayList<>());

        FieldValue fieldValue = new FieldValue(value);

        fieldValues.add(fieldValue);

        valueInfo.fieldInfo.put(field, fieldValues);

        currentVersion.keyInfo.put(key, valueInfo);
    }

    public String get(String key, String field) {

        if (!currentVersion.keyInfo.containsKey(key)) {
            return null;
        }

        ValueInfo valueInfo = currentVersion.keyInfo.get(key);

        if (!valueInfo.fieldInfo.containsKey(field)) {
            return null;
        }

        List<FieldValue> fieldValues = valueInfo.fieldInfo.get(field);

        String fieldValue = fieldValues
                .stream()
                .filter(f -> (f.expiresAt == null) || (f.expiresAt >= System.currentTimeMillis()))
                .max(Comparator.comparingLong(f -> f.expiresAt))
                .map(f -> f.value)
                .orElse(null);


        return fieldValue;
    }

    public void delete(String key, String field) {
    }

    /**
     * @param key
     * @return list in this format <field>(<value>)
     */
    public List<String> scan(String key) {
        return Collections.emptyList();
    }

    /**
     * @param prefix
     * @return list in this format <field>(<value>)
     */
    public List<String> scanWithPrefix(String prefix) {
        return Collections.emptyList();
    }

    public void put(String key, String field, String value, Long timestamp) {
    }

    public void put(String key, String field, String value, Long timestamp, Long ttl) {
    }

    public String get(String key, String field, Long timestamp) {
        return null;
    }

    /**
     * @param key
     * @return list in this format <field>(<value>)
     */
    public List<String> scan(String key, Long timeStamp) {
        return Collections.emptyList();
    }


    /**
     * @param prefix
     * @return list in this format <field>(<value>)
     */
    public List<String> scanWithPrefix(String prefix, Long timeStamp) {
        return Collections.emptyList();
    }

    public void snapshot() {
        this.latestVersion += 1;
        this.currentVersion = new IMDBVersion();
        this.imdbVersions.put(latestVersion, this.currentVersion.clone());
    }

    public void getSnapShot(int snapShotId) {

        if (!imdbVersions.containsKey(snapShotId)) {
            return;
        }
        this.latestVersion = snapShotId;
        this.currentVersion = imdbVersions.get(snapShotId);
    }


    // Internal representation of a field with value, timestamp and TTL
    private static class FieldValue implements Serializable {
        String value;
        long timestamp;
        Long expiresAt; // in seconds, nullable

        public FieldValue(String value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
            this.expiresAt = null;
        }

        public FieldValue(String value, Long timestamp, Long ttl) {
            this.value = value;
            this.timestamp = timestamp;
            this.expiresAt = timestamp + ttl * 1000;
        }

    }

    private static class ValueInfo implements Serializable {
        Map<String, List<FieldValue>> fieldInfo;

        public ValueInfo() {
            fieldInfo = new HashMap<>();
        }
    }

    private static class IMDBVersion implements Cloneable {
        private final Map<String, ValueInfo> keyInfo;

        public IMDBVersion() {
            keyInfo = new HashMap<>();
        }

        @Override
        protected IMDBVersion clone() {
            return null;
        }
    }
}
