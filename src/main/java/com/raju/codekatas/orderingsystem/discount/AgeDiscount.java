package com.raju.codekatas.orderingsystem.discount;

import com.raju.codekatas.orderingsystem.model.Customer;

import java.time.LocalDate;

class AgeDiscount implements PriceDecorator {
    @Override
    public Float calculatePrice(Float basePrice, Customer customer, LocalDate orderDate) {
        if (customer.getAge() > 50) {
            return basePrice * 0.95f; // Additional 5% discount
        }
        return basePrice;
    }
}
