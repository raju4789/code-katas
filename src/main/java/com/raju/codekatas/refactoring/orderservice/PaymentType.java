package com.raju.codekatas.refactoring.orderservice;

import java.util.Arrays;

public enum PaymentType {
    CREDIT_CARD,
    PAYPAL,
    BITCOIN;

    public static PaymentType fromString(String paymentType) throws IllegalArgumentException {
        if (paymentType == null || paymentType.isEmpty()) {
            throw new IllegalArgumentException("paymentType can't be empty or null");
        }


        return Arrays.stream(PaymentType.values())
                .filter(enumValue -> enumValue.name().equalsIgnoreCase(paymentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment type: " + paymentType));

    }


}
