package com.raju.codekatas.refactoring.order;

public interface OrderHandler {

    double BASE_PRICE = 100;

    void process(String order);

    default double calculateFinalPrice(double price, double discount) {
        return price - (price * discount);
    }

    default boolean isVip(String order) {
        return order.startsWith("VIP");
    }
}
