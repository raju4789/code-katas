package com.raju.codekatas.orderingsystem.service;

import com.raju.codekatas.orderingsystem.model.DiscountedOrder;
import com.raju.codekatas.orderingsystem.model.Order;

public interface OrderService {
    DiscountedOrder placeOrder(Order order);
}
