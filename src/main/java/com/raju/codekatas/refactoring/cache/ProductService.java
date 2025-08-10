package com.raju.codekatas.refactoring.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductService {
    private final Map<String, Product> cache = new ConcurrentHashMap<>(); // shared mutable state

    public static void main(String[] args) {
        ProductService service = new ProductService();

        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + " got " + service.getProduct("A1"));
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");
        t1.start();
        t2.start();
    }

    public Product getProduct(String productId) {
        return cache.computeIfAbsent(productId, this::loadProductFromDb);
    }

    private Product loadProductFromDb(String id) {
        System.out.println(Thread.currentThread().getName() + " loading product " + id + " from DB...");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return new Product(id, "Product-" + id, Math.random() * 100);
    }
}

