package com.raju.codekatas.refactoring.payment;

public enum PaymentMethodType {
    CREDIT_CARD("CREDIT_CARD", "Your payment via credit card was successful."),
    PAYPAL("PAYPAL", "Your payment via PayPal was successful."),
    BANK_TRANSFER("BANK_TRANSFER", "Your bank transfer was successful.");

    private final String key;
    private final String successMessage;

    PaymentMethodType(String key, String successMessage) {
        this.key = key;
        this.successMessage = successMessage;
    }

    public String key() {
        return key;
    }

    public String successMessage() {
        return successMessage;
    }
}
