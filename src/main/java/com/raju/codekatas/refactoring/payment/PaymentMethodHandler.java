package com.raju.codekatas.refactoring.payment;

interface PaymentMethodHandler {
    double BASE_PRICE = 100;

    void processPaymentForUser(String userId);

    default double calculateTotalCharge(double fee) {
        return BASE_PRICE * fee;
    }

    default boolean isValidUser(String userId) {
        return userId != null && !userId.isBlank();
    }
}
