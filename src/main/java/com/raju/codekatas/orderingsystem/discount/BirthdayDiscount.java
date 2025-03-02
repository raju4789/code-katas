package com.raju.codekatas.orderingsystem.discount;

import com.raju.codekatas.orderingsystem.model.Customer;

import java.time.LocalDate;

class BirthdayDiscount implements PriceDecorator {
    @Override
    public Float calculatePrice(Float basePrice, Customer customer, LocalDate orderDate) {
        if (customer.getBirthday().equals(orderDate)) {
            return basePrice * 0.95f; // Additional 5% discount
        }
        return basePrice;
    }
}
