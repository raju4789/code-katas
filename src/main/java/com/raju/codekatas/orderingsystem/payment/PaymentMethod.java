package com.raju.codekatas.orderingsystem.payment;

import com.raju.codekatas.orderingsystem.model.Customer;

public interface PaymentMethod {
    boolean pay(Float amount, Customer customer);
}
