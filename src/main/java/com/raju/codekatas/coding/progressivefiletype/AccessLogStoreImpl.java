package com.raju.codekatas.coding.progressivefiletype;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 📘 Progressive Access Log Store
 * <p>
 * ✅ Objective:
 * Track access events by user and support efficient querying and cleanup.
 * <p>
 * 🧩 Features:
 * 1. Record user access with timestamp.
 * 2. Get total access count.
 * 3. Get access count within time range.
 * 4. Cleanup old logs.
 */
interface AccessLogStore {
    void recordAccess(String userId);

    int getAccessCount(String userId);

    int getAccessCount(String userId, long startTime, long endTime);

    void cleanup(long expirationMillis);
}

public class AccessLogStoreImpl implements AccessLogStore {

    /**
     * 🔐 ConcurrentHashMap ensures thread safety for the outer map.
     * Key: userId, Value: List of access timestamps (in milliseconds).
     */
    private final Map<String, List<Long>> userAccessTimes;

    public AccessLogStoreImpl() {
        this.userAccessTimes = new ConcurrentHashMap<>();
    }

    /**
     * 🟢 Stage 1: Record a user's access time.
     * - Stores the current timestamp under the user's list.
     * - Uses synchronization to protect the list from concurrent access.
     */
    @Override
    public void recordAccess(String userId) {
        // Atomically initialize the list if missing
        List<Long> accessTimes = userAccessTimes.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>()));

        // Add current time in milliseconds
        accessTimes.add(System.currentTimeMillis());
    }

    /**
     * 🟢 Stage 1: Return total number of access events for a user.
     */
    @Override
    public int getAccessCount(String userId) {
        List<Long> accessTimes = userAccessTimes.get(userId);
        if (accessTimes == null) return 0;

        // Synchronize on the list while accessing size
        synchronized (accessTimes) {
            return accessTimes.size();
        }
    }

    /**
     * 🕒 Stage 2: Return number of access events in the [startTime, endTime] range.
     */
    @Override
    public int getAccessCount(String userId, long startTime, long endTime) {
        List<Long> accessTimes = userAccessTimes.get(userId);
        if (accessTimes == null) return 0;

        synchronized (accessTimes) {
            return (int) accessTimes.stream()
                    .filter(at -> at >= startTime && at <= endTime)
                    .count();
        }
    }

    /**
     * 🧹 Stage 3: Cleanup all access logs older than expirationMillis.
     * - Any timestamp < (now - expirationMillis) is considered expired.
     * - We use iterator.remove() to avoid ConcurrentModificationException.
     */
    @Override
    public void cleanup(long expirationMillis) {
        long cutoff = System.currentTimeMillis() - expirationMillis;

        for (List<Long> accessTimes : userAccessTimes.values()) {
            synchronized (accessTimes) {
                Iterator<Long> it = accessTimes.iterator();
                while (it.hasNext()) {
                    if (it.next() < cutoff) {
                        it.remove(); // Safely remove during iteration
                    }
                }
            }
        }
    }
}
