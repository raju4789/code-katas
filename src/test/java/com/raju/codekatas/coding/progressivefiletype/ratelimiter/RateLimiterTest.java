package com.raju.codekatas.coding.progressivefiletype.ratelimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    private RateLimiter fixedWindowLimiter;
    private RateLimiter slidingWindowLimiter;

    @BeforeEach
    void setUp() {
        fixedWindowLimiter = new RateLimiterImpl(new FixedWindowStrategy(3, 5));    // 3 reqs per 5 sec
        slidingWindowLimiter = new RateLimiterImpl(new SlidingWindowStrategy(3, 5)); // 3 reqs per 5 sec
    }

    // ----------------------------
    // 🧪 Fixed Window Tests
    // ----------------------------
    @Nested
    class FixedWindowTests {

        @Test
        void shouldAllowUpToLimit() {
            assertTrue(fixedWindowLimiter.allowRequest("user1"));
            assertTrue(fixedWindowLimiter.allowRequest("user1"));
            assertTrue(fixedWindowLimiter.allowRequest("user1"));
            assertFalse(fixedWindowLimiter.allowRequest("user1"));
        }

        @Test
        void shouldIsolatePerUser() {
            for (int i = 0; i < 3; i++) assertTrue(fixedWindowLimiter.allowRequest("userA"));
            assertFalse(fixedWindowLimiter.allowRequest("userA"));

            for (int i = 0; i < 3; i++) assertTrue(fixedWindowLimiter.allowRequest("userB"));
            assertFalse(fixedWindowLimiter.allowRequest("userB"));
        }

        @Test
        void shouldBlockBlacklistedUser() {
            fixedWindowLimiter.blacklist("badUser");
            assertFalse(fixedWindowLimiter.allowRequest("badUser"));
        }

        @Test
        void shouldRestoreAccessAfterBlacklistRemoved() {
            fixedWindowLimiter.blacklist("userX");
            assertFalse(fixedWindowLimiter.allowRequest("userX"));

            fixedWindowLimiter.removeBlacklist("userX");
            assertTrue(fixedWindowLimiter.allowRequest("userX"));
        }

        @Test
        void shouldResetLimiterState() {
            for (int i = 0; i < 3; i++) assertTrue(fixedWindowLimiter.allowRequest("resetUser"));
            assertFalse(fixedWindowLimiter.allowRequest("resetUser"));

            fixedWindowLimiter.reset();

            assertTrue(fixedWindowLimiter.allowRequest("resetUser"));
        }
    }

    // ----------------------------
    // 🧪 Sliding Window Tests
    // ----------------------------
    @Nested
    class SlidingWindowTests {

        @Test
        void shouldAllowUpToLimit() {
            assertTrue(slidingWindowLimiter.allowRequest("user2"));
            assertTrue(slidingWindowLimiter.allowRequest("user2"));
            assertTrue(slidingWindowLimiter.allowRequest("user2"));
            assertFalse(slidingWindowLimiter.allowRequest("user2"));
        }

        @Test
        void shouldEvictOldTimestamps() throws InterruptedException {
            assertTrue(slidingWindowLimiter.allowRequest("slidingUser"));
            Thread.sleep(2000);
            assertTrue(slidingWindowLimiter.allowRequest("slidingUser"));
            Thread.sleep(2000);
            assertTrue(slidingWindowLimiter.allowRequest("slidingUser"));
            assertFalse(slidingWindowLimiter.allowRequest("slidingUser"));

            // After 2 more seconds, first request should expire (2s + 2s + 2s = 6s)
            Thread.sleep(2000);
            assertTrue(slidingWindowLimiter.allowRequest("slidingUser"));
        }

        @Test
        void shouldBlockBlacklistedUser() {
            slidingWindowLimiter.blacklist("evilUser");
            assertFalse(slidingWindowLimiter.allowRequest("evilUser"));
        }

        @Test
        void shouldResetSlidingWindowState() {
            for (int i = 0; i < 3; i++) assertTrue(slidingWindowLimiter.allowRequest("slidingReset"));
            assertFalse(slidingWindowLimiter.allowRequest("slidingReset"));

            slidingWindowLimiter.reset();
            assertTrue(slidingWindowLimiter.allowRequest("slidingReset"));
        }
    }
}
