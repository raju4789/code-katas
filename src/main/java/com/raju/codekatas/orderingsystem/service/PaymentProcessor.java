package com.raju.codekatas.orderingsystem.service;

import com.raju.codekatas.orderingsystem.model.Customer;
import com.raju.codekatas.orderingsystem.model.PaymentDetails;

public interface PaymentProcessor {
    void processPayments(PaymentDetails paymentDetails, Customer customer, Float orderTotal);
}
