package com.raju.codekatas.orderingsystem.discount;

import com.raju.codekatas.orderingsystem.model.Customer;

import java.time.LocalDate;

public interface PriceDecorator {
    Float calculatePrice(Float basePrice, Customer customer, LocalDate orderDate);
}
