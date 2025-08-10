package com.raju.codekatas.coding.progressivefiletype.ratelimiter;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface RateLimitingStrategy {
    boolean allow(String userId, long currentTimeMillis);

    void reset();
}

class FixedWindowStrategy implements RateLimitingStrategy {

    private final int maxRequests;
    private final int windowSizeInSeconds;

    private final Map<String, UserInfo> userRequestInfo;

    private final ReentrantReadWriteLock readWriteLock;

    public FixedWindowStrategy(int maxRequests, int windowSizeInSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.userRequestInfo = new HashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean allow(String userId, long currentTimeMillis) {

        readWriteLock.writeLock().lock();

        try {
            UserInfo userInfo = userRequestInfo.get(userId);
            long now = System.currentTimeMillis();
            if ((userInfo == null) || now > userInfo.getCurrentWindowStartTime() + windowSizeInSeconds * 1000L) {
                userInfo = new UserInfo(1, getWindowStartTime());
            } else {
                if (userInfo.getRequestCount() >= maxRequests) {
                    return false;
                }

                userInfo.setRequestCount(userInfo.getRequestCount() + 1);
            }
            userRequestInfo.put(userId, userInfo);
            return true;
        } finally {
            readWriteLock.writeLock().unlock();
        }


    }

    private long getWindowStartTime() {
        long now = System.currentTimeMillis();
        return now - (now % windowSizeInSeconds * 1000L);
    }

    @Override
    public void reset() {
        readWriteLock.writeLock().lock();
        try {
            userRequestInfo.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private static class UserInfo {
        private int requestCount;
        private long currentWindowStartTime;

        public UserInfo(int requestCount, long currentWindowStartTime) {
            this.requestCount = requestCount;
            this.currentWindowStartTime = currentWindowStartTime;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        public long getCurrentWindowStartTime() {
            return currentWindowStartTime;
        }

        public void setCurrentWindowStartTime(long currentWindowStartTime) {
            this.currentWindowStartTime = currentWindowStartTime;
        }
    }

}

/**
 * 📄 Problem Statement – Sliding Window Rate Limiter
 * <p>
 * Implement a sliding window rate limiter that improves upon the fixed window approach by
 * evenly distributing allowed requests over time. Instead of hard window boundaries, the
 * sliding window divides time into smaller intervals (buckets) and keeps a running count
 * of requests across these buckets.
 * <p>
 * 🧩 Objective:
 * For each request, allow it only if the number of requests in the last N seconds (sliding window)
 * is less than or equal to the configured maxRequests.
 * <p>
 * ✅ Constraints:
 * - You will receive a userId and the current timestamp (in millis).
 * - Each user should be rate-limited independently.
 * - Assume the smallest time unit used is 1 second (each bucket is 1 second).
 * - No need for thread-safety initially.
 * <p>
 * 💡 Example:
 * RateLimiter rl = new RateLimiterImpl(3, 10); // 3 requests per 10 seconds
 * rl.allowRequest("user1"); // true
 * rl.allowRequest("user1"); // true
 * rl.allowRequest("user1"); // true
 * rl.allowRequest("user1"); // false (within 10 sec window)
 * <p>
 * // After 6 seconds and 1 request has fallen out
 * rl.allowRequest("user1"); // true
 */
class SlidingWindowStrategy implements RateLimitingStrategy {

    private final int maxRequests;
    private final int windowSizeInSeconds;
    private final Map<String, Deque<Long>> usersRequestInfo;
    private final ReentrantReadWriteLock readWriteLock;

    public SlidingWindowStrategy(int maxRequests, int windowSizeInSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.usersRequestInfo = new HashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean allow(String userId, long currentTimeMillis) {
        readWriteLock.writeLock().lock();

        try {
            Deque<Long> userTimeStamps = usersRequestInfo.computeIfAbsent(userId, k -> new LinkedList<>());
            long windowStartTime = currentTimeMillis - windowSizeInSeconds * 1000L;

            // Remove old requests outside the sliding window
            while (!userTimeStamps.isEmpty() && userTimeStamps.peekFirst() < windowStartTime) {
                userTimeStamps.pollFirst();
            }

            if (userTimeStamps.size() >= maxRequests) {
                return false;
            } else {
                userTimeStamps.addLast(currentTimeMillis);
                return true;
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void reset() {
        readWriteLock.writeLock().lock();
        try {
            usersRequestInfo.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}


