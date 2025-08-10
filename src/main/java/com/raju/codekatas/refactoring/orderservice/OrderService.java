package com.raju.codekatas.refactoring.orderservice;

import java.util.Map;

public class OrderService {

    private static final double DISCOUNT_PERCENTAGE = 0.9;

    private final PersistentService persistentService;

    private final Map<PaymentType, PaymentHandler> paymentHandlerMap;

    OrderService(PersistentService persistentService, Map<PaymentType, PaymentHandler> paymentHandlerMap) {

        this.persistentService = persistentService;
        this.paymentHandlerMap = paymentHandlerMap;
    }

    private static void validate(Order order, Email userEmail, PhoneNumber userPhone) {
        order.validate();
        userEmail.validate();
        userPhone.validate();
    }

    public void processOrder(Order order, String paymentType, Email userEmail, PhoneNumber userPhone) {

        try {
            validate(order, userEmail, userPhone);

            PaymentType type = PaymentType.fromString(paymentType);


            PaymentHandler paymentHandler = paymentHandlerMap.get(type);

            if (paymentHandler == null) {
                throw new IllegalArgumentException("Unknown payment type: " + paymentType);
            }

            double total = order.calculateTotalPrice(order, DISCOUNT_PERCENTAGE);

            paymentHandler.processOrder(total);

            persistentService.save(order);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}

