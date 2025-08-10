package com.raju.codekatas.coding.progressivefiletype;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUrlShortenerTest {
    private InMemoryUrlShortener shortener;

    @BeforeEach
    void setUp() {
        shortener = new InMemoryUrlShortener();
    }

    @Test
    @DisplayName("should be able to shorten and restore a URL")
    void testShortenAndRestore_basic() {
        Optional<String> shortUrl = shortener.shorten("https://example.com/abc");
        assertTrue(shortUrl.isPresent());
        Optional<String> restored = shortener.restore(shortUrl.get());
        assertTrue(restored.isPresent());
        assertEquals("https://example.com/abc", restored.get());
    }

    @Test
    @DisplayName("should return same short URL for the same long URL")
    void testShorten_sameUrlReturnsSameShortUrl() {
        Optional<String> url1 = shortener.shorten("https://example.com/abc");
        Optional<String> url2 = shortener.shorten("https://example.com/abc");
        assertEquals(url1, url2);
    }

    @Test
    @DisplayName("should return different short URLs for different long URLs")
    void testShorten_differentUrlsReturnDifferentShortUrls() {
        Optional<String> url1 = shortener.shorten("https://example.com/abc");
        Optional<String> url2 = shortener.shorten("https://example.com/def");
        assertNotEquals(url1, url2);
    }

    @Test
    @DisplayName("should return short URL based in alias if provided")
    void testShortenWithCustomAlias_success() {
        Optional<String> shortUrl = shortener.shorten("https://example.com", "go");
        assertTrue(shortUrl.isPresent());
        assertTrue(shortUrl.get().endsWith("go"));
        Optional<String> restored = shortener.restore("go");
        assertTrue(restored.isPresent());
        assertEquals("https://example.com", restored.get());
    }

    @Test
    @DisplayName("should throw exception if alias already exists")
    void testShortenWithCustomAlias_collision() {
        shortener.shorten("https://example.com", "go");
        assertThrows(IllegalArgumentException.class, () -> shortener.shorten("https://other.com", "go"));
    }

    @Test
    @DisplayName("should return empty for blank alias")
    void testShortenWithCustomAlias_blankAlias() {
        Optional<String> result = shortener.shorten("https://example.com", " ");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty for invalid URL")
    void testShorten_invalidUrl() {
        Optional<String> result = shortener.shorten("htp://bad-url");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty for short URL that expired")
    void testShortenWithTTL_expiry() throws InterruptedException {
        Optional<String> shortUrl = shortener.shorten("https://expiring.com", 500);
        assertTrue(shortUrl.isPresent());
        Thread.sleep(600);
        Optional<String> restored = shortener.restore(shortUrl.get());
        assertTrue(restored.isEmpty());
    }

    @Test
    @DisplayName("should return access count correctly")
    void testAccessCount_tracking() {
        Optional<String> shortUrl = shortener.shorten("https://example.com/track");
        assertTrue(shortUrl.isPresent());
        String key = shortUrl.get();
        assertEquals(0, shortener.getAccessCount(key));
        shortener.restore(key);
        shortener.restore(key);
        assertEquals(2, shortener.getAccessCount(key));
    }
}

