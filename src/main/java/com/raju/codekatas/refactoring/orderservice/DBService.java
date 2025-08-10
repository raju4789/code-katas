package com.raju.codekatas.refactoring.orderservice;

interface PersistentService {

    void save(Order order);
}

public class DBService implements PersistentService {
    @Override
    public void save(Order order) {
        try {
            // Simulate saving to DB
            Thread.sleep(100);
            System.out.println("Order saved successfully");
        } catch (InterruptedException e) {
            System.out.println("Saving order interrupted");
        }
    }
}
