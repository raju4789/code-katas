package com.raju.codekatas.orderingsystem.model;

import com.raju.codekatas.orderingsystem.payment.PaymentMethod;

import java.util.List;

public class PaymentDetails {
    private final List<PaymentMethod> paymentMethods;
    private final List<Float> amounts;

    public PaymentDetails(List<PaymentMethod> paymentMethods, List<Float> amounts) {
        this.paymentMethods = paymentMethods;
        this.amounts = amounts;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public List<Float> getAmounts() {
        return amounts;
    }

    public Float getTotalAmount() {
        return amounts.stream().reduce(0.0f, Float::sum);
    }
}
