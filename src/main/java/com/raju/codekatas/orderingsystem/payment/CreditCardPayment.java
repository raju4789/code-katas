package com.raju.codekatas.orderingsystem.payment;

import com.raju.codekatas.orderingsystem.model.Customer;

public class CreditCardPayment implements PaymentMethod {
    @Override
    public boolean pay(Float amount, Customer customer) {
        System.out.println("Paid " + amount + " using credit card.");
        return true;
    }
}
