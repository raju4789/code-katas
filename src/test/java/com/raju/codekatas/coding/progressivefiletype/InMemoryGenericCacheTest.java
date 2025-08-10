package com.raju.codekatas.coding.progressivefiletype;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryGenericCacheTest {

    private InMemoryGenericCache<String, String> cache;

    @BeforeEach
    void setup() {
        cache = new InMemoryGenericCache<>(3);
    }

    @Test
    @DisplayName("should be able to add key, value to cache without expiry")
    void test1() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    @DisplayName("should return null if key doesn't exist in the cache")
    void test2() {
        assertNull(cache.get("key1"));
    }

    @Test
    @DisplayName("should be able to add key, value with ttl to cache")
    void test3() {
        cache.put("key1", "value1", 1000L);
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    @DisplayName("should return null if key expired in the cache")
    void test4() throws InterruptedException {
        cache.put("key1", "value1", 1000L);
        Thread.sleep(2000);
        assertNull(cache.get("key1"));
    }

    @Test
    @DisplayName("should return value if key is not expired in the cache")
    void test7() throws InterruptedException {
        cache.put("key1", "value1", 7000L);
        Thread.sleep(2000);
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    @DisplayName("should return null if key is removed from cache")
    void test5() {
        cache.put("key1", "value1", 1000L);
        cache.remove("key1");
        assertNull(cache.get("key1"));
    }

    @Test
    @DisplayName("should return null for any key if we clear the cache")
    void test6() throws InterruptedException {
        cache.put("key1", "value1", 5000L);
        cache.put("key2", "value1", 5000L);
        cache.put("key3", "value1", 5000L);
        cache.put("key4", "value1", 5000L);

        cache.clear();
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
        assertNull(cache.get("key3"));
        assertNull(cache.get("key4"));


    }

    @Test
    @DisplayName("should evict oldest entry when capacity exceeded")
    void shouldEvictOldestEntryWhenCapacityExceeded() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4"); // Should evict key1
        assertNull(cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
        assertEquals("value4", cache.get("key4"));
    }

    @Test
    @DisplayName("should overwrite value for existing key")
    void shouldOverwriteValueForExistingKey() {
        cache.put("key1", "value1");
        cache.put("key1", "value2");
        assertEquals("value2", cache.get("key1"));
    }

    @Test
    @DisplayName("should reset TTL when updating key")
    void shouldResetTTLWhenUpdatingKey() throws InterruptedException {
        cache.put("key1", "value1", 500L);
        Thread.sleep(300);
        cache.put("key1", "value2", 1000L);
        Thread.sleep(600);
        assertEquals("value2", cache.get("key1")); // Should not be expired
    }

    @Test
    @DisplayName("should handle null key and value")
    void shouldHandleNullKeyAndValue() {
        assertNull(cache.get(null));
        cache.put(null, null);
        assertNull(cache.get(null)); // Assuming cache does not store null keys
    }

    @Test
    @DisplayName("should handle concurrent put and get operations safely")
    void shouldHandleConcurrentPutAndGet() throws InterruptedException {
        // Use a cache large enough to hold all keys
        cache = new InMemoryGenericCache<>(1000);
        int threadCount = 10;
        int iterations = 100;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    String key = "key" + (threadNum * iterations + j);
                    cache.put(key, "value" + j);
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        // After all threads complete, check a few keys
        assertEquals("value99", cache.get("key999"));
        assertEquals("value0", cache.get("key0"));
        assertEquals("value50", cache.get("key50"));
    }
}
