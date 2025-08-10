package com.raju.codekatas.coding.progressivefiletype.ratelimiter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 📄 Problem Statement:
 * <p>
 * Design a progressive RateLimiter system to control incoming API requests per user/IP.
 * The implementation should support extensible strategies added in multiple stages.
 * <p>
 * 🧩 Features:
 * <p>
 * Stage 1️⃣: Fixed window counter
 * - Allow at most N requests per user per fixed time window (in seconds).
 * <p>
 * Stage 2️⃣: Sliding window counter
 * - Distribute request limits more evenly using a sliding time window.
 * <p>
 * Stage 3️⃣: Blacklisting
 * - Support blocking userIds/IPs from making any requests.
 * <p>
 * Stage 4️⃣: Reset
 * - Allow clearing internal state and blacklists.
 * <p>
 * ✅ Constraints:
 * - Requests identified by `userId` or `ip`
 * - Time tracked using System.currentTimeMillis()
 * - Single-threaded (for now)
 * <p>
 * 💡 Examples:
 * <p>
 * RateLimiter rl = new RateLimiterImpl(3, 10); // 3 requests per 10 seconds
 * rl.allowRequest("user1"); // true
 * rl.allowRequest("user1"); // true
 * rl.allowRequest("user1"); // true
 * rl.allowRequest("user1"); // false
 * <p>
 * rl.blacklist("user1");
 * rl.allowRequest("user1"); // false
 * rl.removeBlacklist("user1");
 * rl.allowRequest("user1"); // depends on window
 * <p>
 * rl.reset();
 * rl.allowRequest("user1"); // true
 */


interface RateLimiter {
    boolean allowRequest(String userId);

    void blacklist(String userId);

    void removeBlacklist(String userId);

    void reset();
}


public class RateLimiterImpl implements RateLimiter {

    private final Set<String> blacklist;
    private final RateLimitingStrategy strategy;
    private final ReentrantReadWriteLock readWriteLock;

    public RateLimiterImpl(RateLimitingStrategy strategy) {
        this.strategy = strategy;
        this.blacklist = new HashSet<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean allowRequest(String userId) {
        readWriteLock.writeLock().lock();
        try {
            if (blacklist.contains(userId)) return false;
            return strategy.allow(userId, System.currentTimeMillis());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    @Override
    public void blacklist(String userId) {
        readWriteLock.writeLock().lock();

        try {
            blacklist.add(userId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void removeBlacklist(String userId) {
        readWriteLock.writeLock().lock();

        try {
            blacklist.remove(userId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void reset() {
        readWriteLock.writeLock().lock();

        try {
            blacklist.clear();
            strategy.reset();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}


