package com.raju.codekatas.orderingsystem.discount;

import com.raju.codekatas.orderingsystem.model.Customer;

import java.time.DayOfWeek;
import java.time.LocalDate;

class WeekendDiscount implements PriceDecorator {
    @Override
    public Float calculatePrice(Float basePrice, Customer customer, LocalDate orderDate) {
        if (orderDate.getDayOfWeek() == DayOfWeek.SATURDAY || orderDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return basePrice * 0.9f;
        }
        return basePrice;
    }
}
