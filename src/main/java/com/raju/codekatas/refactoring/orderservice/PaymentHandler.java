package com.raju.codekatas.refactoring.orderservice;

public interface PaymentHandler {

    void processOrder(double total);
}

class CreditCardHandler implements PaymentHandler {

    @Override
    public void processOrder(double total) {
        System.out.println("Processing credit card payment of $" + total);
    }
}

class PaypalHandler implements PaymentHandler {

    @Override
    public void processOrder(double total) {
        System.out.println("Processing PayPal payment of $" + total);
    }
}

class BitcoinHandler implements PaymentHandler {

    @Override
    public void processOrder(double total) {
        System.out.println("Processing Bitcoin payment of $" + total);
    }
}
