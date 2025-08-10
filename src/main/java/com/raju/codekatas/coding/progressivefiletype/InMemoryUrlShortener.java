package com.raju.codekatas.coding.progressivefiletype;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 🚀 Problem Statement: Build a Scalable URL Shortener
 * <p>
 * You are asked to implement a simple version of a URL shortener (like Bit.ly or TinyURL).
 * This system should shorten long URLs into short, unique identifiers and allow retrieving them.
 * <p>
 * The solution should evolve over time in stages — each building upon the last.
 * Assume this is an in-memory, single-instance prototype (no persistent DB or distributed system).
 * <p>
 * 🧩 Functional Stages:
 * <p>
 * 🔹 Stage 1: Basic Encode & Decode
 * - Implement shorten(String longUrl): String
 * - Always returns the same short code for the same input URL
 * - Implement restore(String shortUrl): String
 * - Returns original URL from the short version
 * <p>
 * 🔹 Stage 2: Base62 Encoding
 * <p>
 * ✅ Goal:
 * Use a Base62 encoding scheme to generate short, unique URLs
 * based on an auto-incrementing counter.
 * <p>
 * ✅ Why Base62?
 * Base62 uses characters [0-9][a-z][A-Z] (total 62 characters),
 * allowing compact, URL-friendly short codes like "b9", "Z3", etc.
 * <p>
 * ✅ How it works:
 * - Maintain an AtomicLong counter (e.g., starts at 1)
 * - For each new long URL:
 * 1. Increment the counter
 * 2. Convert the number to a Base62 string
 * 3. Use that as the short key and store the mapping
 * <p>
 * ✅ Example:
 * Counter: 125 → Base62: "21" → Short URL: http://sho.rt/21
 * <p>
 * ✅ Expected logic:
 * long id = counter.getAndIncrement();
 * String shortKey = encodeBase62(id);
 * shortToLong.put(shortKey, longUrl);
 * longToShort.put(longUrl, shortKey);
 * return DOMAIN + shortKey;
 * <p>
 * 🔹 Stage 3: Custom Alias Support
 * <p>
 * ✅ Goal:
 * Allow users to specify their own short link alias.
 * <p>
 * ✅ Method signature:
 * String shorten(String longUrl, String customAlias);
 * <p>
 * ✅ Expected behavior:
 * - If alias (e.g., "go") is available:
 * -> Save the mapping: shortToLong.put("go", longUrl)
 * -> Return short URL: http://sho.rt/go
 * - If alias is already in use:
 * -> Throw exception: IllegalArgumentException("Alias already in use")
 * <p>
 * ✅ Example:
 * shorten("https://example.com", "go"); // returns http://sho.rt/go
 * restore("go");                        // returns https://example.com
 * shorten("https://other.com", "go");   // throws exception (alias taken)
 * <p>
 * ✅ Things to handle:
 * - Prevent collisions for aliases
 * - Ensure restore works for both auto and custom aliases
 * <p>
 * <p>
 * 🔹 Stage 4: Thread Safety
 * - Ensure the service is safe under concurrent read/write access
 * - Use read-write locking to ensure correctness
 * <p>
 * <p>
 * <p>
 * 🔹 Stage 5 (Optional): TTL Support
 * - Support expiry on short URLs: shorten(String longUrl, long ttlMillis)
 * - On restore, return null if expired
 * <p>
 * <p>
 * <p>
 * 🔹 Stage 6 (Optional): Access Tracking
 * - Track and return the number of times each short URL has been accessed
 * <p>
 * <p>
 * 🧪 Example Usage:
 * <p>
 * UrlShortener shortener = new InMemoryUrlShortener();
 * <p>
 * String shortUrl = shortener.shorten("https://example.com/some/long/url");
 * String original = shortener.restore(shortUrl);
 * // original == "https://example.com/some/long/url"
 * <p>
 * String aliasUrl = shortener.shorten("https://example.com", "go");
 * shortener.restore("go"); // returns "https://example.com"
 * <p>
 * String shortUrl2 = shortener.shorten("https://expiring.com", 1000); // 1 sec TTL
 * Thread.sleep(1500);
 * shortener.restore(shortUrl2); // returns null (expired)
 * <p>
 * shortener.getAccessCount(shortUrl); // returns number of times restore was called
 * <p>
 * <p>
 * 📌 Constraints:
 * - Use only core Java libraries (no frameworks)
 * - Use clean, modular, testable, and maintainable code
 * - Class should be generic enough to extend for persistence or analytics later
 */
interface UrlShortener {
    Optional<String> shorten(String longUrl);                          // Stage 1

    Optional<String> restore(String shortUrl);                         // Stage 1

    Optional<String> shorten(String longUrl, String customAlias);      // Stage 3

    Optional<String> shorten(String longUrl, long ttlMillis);          // Stage 5

    int getAccessCount(String shortUrl);                     // Stage 6
}

public class InMemoryUrlShortener implements UrlShortener {

    private static final String SERVICE_DOMAIN = "sho.rt";
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MAX_URL_LENGTH = 2048;
    private static final List<String> supportedProtocols = List.of("http", "https");


    private final AtomicLong counter = new AtomicLong(238328);

    private final Map<String, UrlEntry> shortToLong = new HashMap<>();
    private final Map<String, UrlEntry> longToShort = new HashMap<>();
    private final Map<String, Integer> accessCount = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Optional<String> shorten(String longUrl) {
        Optional<URL> normalisedUrl = validateAndNormalizeUrl(longUrl);
        if (normalisedUrl.isEmpty()) return Optional.empty();

        lock.readLock().lock(); // 🧵
        try {
            UrlEntry cached = longToShort.get(normalisedUrl.get().toString());
            if (cached != null) return Optional.of(cached.getShortUrl());
        } finally {
            lock.readLock().unlock(); // 🧵
        }

        String shortKey = encodeBase62(counter.getAndIncrement());
        String shortenUrl = buildUrl(normalisedUrl.get(), shortKey);
        UrlEntry urlEntry = new UrlEntry(longUrl, normalisedUrl.get().toString(), shortenUrl);

        lock.writeLock().lock(); // 🧵
        try {
            // Re-check after acquiring write lock
            UrlEntry existing = longToShort.get(normalisedUrl.get().toString());
            if (existing != null) return Optional.of(existing.getShortUrl());

            longToShort.put(normalisedUrl.get().toString(), urlEntry);
            shortToLong.put(shortKey, urlEntry);
        } finally {
            lock.writeLock().unlock(); // 🧵
        }

        return Optional.of(shortenUrl);
    }

    @Override
    public Optional<String> restore(String shortUrl) {
        Optional<String> shortKey = extractShortKey(shortUrl);
        if (shortKey.isEmpty()) return Optional.empty();

        UrlEntry shortUrlEntry;
        lock.readLock().lock(); // 🧵
        try {
            shortUrlEntry = shortToLong.get(shortKey.get());
            if (shortUrlEntry == null) return Optional.empty();
        } finally {
            lock.readLock().unlock(); // 🧵
        }

        lock.writeLock().lock(); // 🧵
        try {
            // Check if expired
            if (shortUrlEntry.expiresAt == null || shortUrlEntry.expiresAt >= System.currentTimeMillis()) {
                accessCount.put(shortKey.get(), accessCount.getOrDefault(shortKey.get(), 0) + 1);
                return Optional.of(shortUrlEntry.getOriginalUrl());
            } else {
                // Clean up expired entry
                longToShort.remove(shortUrlEntry.getNormalizedUrl());
                shortToLong.remove(shortKey.get());
                accessCount.remove(shortKey.get());
                return Optional.empty();
            }
        } finally {
            lock.writeLock().unlock(); // 🧵
        }
    }

    @Override
    public Optional<String> shorten(String longUrl, String customAlias) {
        if (customAlias == null || customAlias.isBlank()) return Optional.empty();

        Optional<URL> normalizedUrl = validateAndNormalizeUrl(longUrl);
        if (normalizedUrl.isEmpty()) return Optional.empty();

        lock.writeLock().lock(); // 🧵
        try {
            if (shortToLong.containsKey(customAlias)) {
                throw new IllegalArgumentException("Alias already in use");
            }

            String shortUrl = buildUrl(normalizedUrl.get(), customAlias);
            UrlEntry urlEntry = new UrlEntry(longUrl, normalizedUrl.get().toString(), shortUrl);
            longToShort.put(normalizedUrl.get().toString(), urlEntry);
            shortToLong.put(customAlias, urlEntry);

            return Optional.of(shortUrl);
        } finally {
            lock.writeLock().unlock(); // 🧵
        }
    }

    @Override
    public Optional<String> shorten(String longUrl, long ttlMillis) {
        Optional<URL> normalisedUrl = validateAndNormalizeUrl(longUrl);
        if (normalisedUrl.isEmpty()) return Optional.empty();

        lock.readLock().lock(); // 🧵
        try {
            UrlEntry cached = longToShort.get(normalisedUrl.get().toString());
            if (cached != null) return Optional.of(cached.getShortUrl());
        } finally {
            lock.readLock().unlock(); // 🧵
        }

        String shortKey = encodeBase62(counter.getAndIncrement());
        String shortenUrl = buildUrl(normalisedUrl.get(), shortKey);
        UrlEntry urlEntry = new UrlEntry(longUrl, normalisedUrl.get().toString(), shortenUrl, ttlMillis);

        lock.writeLock().lock(); // 🧵
        try {
            UrlEntry existing = longToShort.get(normalisedUrl.get().toString());
            if (existing != null) return Optional.of(existing.getShortUrl());

            longToShort.put(normalisedUrl.get().toString(), urlEntry);
            shortToLong.put(shortKey, urlEntry);
        } finally {
            lock.writeLock().unlock(); // 🧵
        }

        return Optional.of(shortenUrl);
    }

    @Override
    public int getAccessCount(String shortUrl) {
        Optional<String> shortKey = extractShortKey(shortUrl);
        if (shortKey.isEmpty()) return 0;

        lock.readLock().lock(); // 🧵
        try {
            return accessCount.getOrDefault(shortKey.get(), 0);
        } finally {
            lock.readLock().unlock(); // 🧵
        }
    }

    // 🔧 Helper for Base62 encoding from numeric ID (Stage 2)
    private String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(ALPHABET.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }

    private String buildUrl(URL normalizedUrl, String key) {
        String protocol = normalizedUrl.getProtocol();

        return """
                %s://%s/%s
                """.formatted(protocol, SERVICE_DOMAIN, key);
    }

    private Optional<String> extractShortKey(String shortUrl) {

        if (shortUrl == null || shortUrl.isEmpty()) {
            return Optional.empty();
        }

        int idx = shortUrl.lastIndexOf('/');

        if (idx != -1 && idx + 1 < shortUrl.length()) {
            int shortKeyStartIndex = idx + 1;
            return Optional.of(shortUrl.substring(shortKeyStartIndex));
        }
        // Assume it's an alias
        return Optional.of(shortUrl);
    }

    /**
     * Validates and normalizes a user-provided URL.
     *
     * @param rawUrl the input URL string
     * @return an Optional containing the normalized URL if valid; otherwise empty
     */
    private Optional<URL> validateAndNormalizeUrl(String rawUrl) {
        // 1️⃣ Null or blank check , or excessive length
        if (rawUrl == null || rawUrl.isBlank() || rawUrl.length() > MAX_URL_LENGTH) {
            return Optional.empty();
        }

        try {
            // 2️⃣ Parse and validate URL structure
            URL url = new URL(rawUrl);
            URI uri = url.toURI();  // also ensures no illegal characters

            // 3️⃣ Reject internal/service domain to avoid redirect loops
            String host = url.getHost().toLowerCase();
            if (host.endsWith(SERVICE_DOMAIN)) {
                return Optional.empty();
            }

            // 4️⃣ Protocol whitelist: only allow HTTP and HTTPS
            String protocol = url.getProtocol().toLowerCase();
            if (!supportedProtocols.contains(protocol)) {
                return Optional.empty();
            }

            // 5️⃣ Determine if port is “default” for the protocol
            int port = url.getPort();
            boolean isDefaultPort =
                    (protocol.equals("http") && (port == 80 || port == -1)) ||
                            (protocol.equals("https") && (port == 443 || port == -1));

            // 6️⃣ Rebuild the URL in canonical form:
            //    - lowercase host
            //    - omit default ports
            //    - preserve path, query, and fragment
            StringBuilder normalized = new StringBuilder();
            normalized
                    .append(protocol)
                    .append("://")
                    .append(host);

            // Append non-default port, if present
            if (port != -1 && !isDefaultPort) {
                normalized.append(":").append(port);
            }

            // Ensure path always begins with “/”
            String path = url.getPath();
            normalized.append((path == null || path.isEmpty()) ? "/" : path);

            // Append query string if present
            if (url.getQuery() != null) {
                normalized.append("?").append(url.getQuery());
            }

            // Append fragment if present
            if (url.getRef() != null) {
                normalized.append("#").append(url.getRef());
            }

            String normalizedUrl = normalized.toString();
            if (normalizedUrl.length() > MAX_URL_LENGTH) {
                return Optional.empty();
            }

            return Optional.of(new URL(normalized.toString()));
        } catch (MalformedURLException | URISyntaxException e) {
            // 7️⃣ Reject malformed or illegal URL inputs
            return Optional.empty();
        }
    }

    private static class UrlEntry {
        private String originalUrl;    // stored for redirect or audit
        private String normalizedUrl;  // used for lookups
        private String shortUrl;
        private Long ttl;
        private Long expiresAt;

        public UrlEntry(String originalUrl, String normalizedUrl, String shortUrl) {
            this.originalUrl = originalUrl;
            this.normalizedUrl = normalizedUrl;
            this.shortUrl = shortUrl;
            this.ttl = null;
            this.expiresAt = null;
        }

        public UrlEntry(String originalUrl, String normalizedUrl, String shortUrl, Long ttl) {
            this.originalUrl = originalUrl;
            this.normalizedUrl = normalizedUrl;
            this.shortUrl = shortUrl;
            this.ttl = ttl;
            this.expiresAt = System.currentTimeMillis() + ttl;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }

        public String getNormalizedUrl() {
            return normalizedUrl;
        }

        public void setNormalizedUrl(String normalizedUrl) {
            this.normalizedUrl = normalizedUrl;
        }

        public String getShortUrl() {
            return shortUrl;
        }

        public void setShortUrl(String shortUrl) {
            this.shortUrl = shortUrl;
        }

        public Long getTtl() {
            return ttl;
        }

        public void setTtl(Long ttl) {
            this.ttl = ttl;
        }

        public Long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(Long expiresAt) {
            this.expiresAt = expiresAt;
        }
    }

}


