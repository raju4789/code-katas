package com.raju.codekatas.refactoring.cachewithexpiry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductService {
    private static final long expiryMillis = 5000;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public Product getProduct(String productId) {

        CacheEntry entry = cache.get(productId);
        if (entry != null && !entry.isExpired()) {
            return entry.product;
        }

        Product product = loadProductFromDb(productId);
        CacheEntry newEntry = new CacheEntry(product, expiryMillis);
        CacheEntry existing = cache.putIfAbsent(productId, newEntry);
        if (existing != null && !existing.isExpired()) {
            return existing.product;
        }
        return product;

    }

    private Product loadProductFromDb(String id) {
        System.out.println(Thread.currentThread().getName() + " loading product " + id + " from DB...");
        try {
            Thread.sleep(100); // simulate DB load delay
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Product(id, "Product-" + id, Math.random() * 100);
    }

    private static class CacheEntry {
        private final Product product;
        private final long expiryTime;

        CacheEntry(Product product, long expiryMillis) {
            this.product = product;
            this.expiryTime = System.currentTimeMillis() + expiryMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}

