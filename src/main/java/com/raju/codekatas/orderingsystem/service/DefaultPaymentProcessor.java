package com.raju.codekatas.orderingsystem.service;

import com.raju.codekatas.orderingsystem.exception.PaymentProcessingException;
import com.raju.codekatas.orderingsystem.model.Customer;
import com.raju.codekatas.orderingsystem.model.PaymentDetails;
import com.raju.codekatas.orderingsystem.payment.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultPaymentProcessor implements PaymentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPaymentProcessor.class);

    @Override
    public void processPayments(PaymentDetails paymentDetails, Customer customer, Float orderTotal) {
        logger.info("Processing payments for customer: {}", customer.getName());

        // Validate that the total split amount matches the order total
        if (!paymentDetails.getTotalAmount().equals(orderTotal)) {
            throw new PaymentProcessingException("Split payment amounts do not match the order total.");
        }

        // Process each payment
        List<PaymentMethod> methods = paymentDetails.getPaymentMethods();
        List<Float> amounts = paymentDetails.getAmounts();

        for (int i = 0; i < methods.size(); i++) {
            if (!methods.get(i).pay(amounts.get(i), customer)) {
                throw new PaymentProcessingException("Payment failed");
            }
            logger.info("Payment of {} processed using {}", amounts.get(i), methods.get(i).getClass().getSimpleName());
        }

    }
}