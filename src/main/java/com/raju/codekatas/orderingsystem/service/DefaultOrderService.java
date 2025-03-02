package com.raju.codekatas.orderingsystem.service;

import com.raju.codekatas.orderingsystem.discount.PriceDecorator;
import com.raju.codekatas.orderingsystem.exception.OrderProcessingException;
import com.raju.codekatas.orderingsystem.exception.PaymentProcessingException;
import com.raju.codekatas.orderingsystem.model.Customer;
import com.raju.codekatas.orderingsystem.model.DiscountedOrder;
import com.raju.codekatas.orderingsystem.model.Order;
import com.raju.codekatas.orderingsystem.model.PaymentDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DefaultOrderService implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultOrderService.class);

    private final List<PriceDecorator> discountDecorators;
    private final PaymentProcessor paymentProcessor;

    public DefaultOrderService(PaymentProcessor paymentProcessor, List<PriceDecorator> discountDecorators) {
        this.paymentProcessor = paymentProcessor;
        this.discountDecorators = discountDecorators;
    }

    private Float calculateFinalPrice(Float basePrice, Order order, LocalDate orderDate, List<String> discountBreakdown) {
        Float finalPrice = basePrice;
        Customer customer = order.getCustomer();
        for (PriceDecorator decorator : discountDecorators) {
            Float newPrice = decorator.calculatePrice(finalPrice, customer, orderDate);
            if (!newPrice.equals(finalPrice)) {
                discountBreakdown.add(decorator.getClass().getSimpleName() + " applied: " + (finalPrice - newPrice));
            }
            finalPrice = newPrice;
        }
        return finalPrice;
    }

    private void validatePaymentDetails(PaymentDetails paymentDetails, Float orderTotal) {
        if (!paymentDetails.getTotalAmount().equals(orderTotal)) {
            throw new OrderProcessingException("Split payment amounts do not match the order total.");
        }
    }

    @Override
    public DiscountedOrder placeOrder(Order order) {
        logger.info("Placing order for customer: {}", order.getCustomer().getName());

        try {
            // Calculate the final price after applying discounts
            List<String> discountBreakdown = new ArrayList<>();
            Float discountedPrice = calculateFinalPrice(order.getTotalPrice(), order, LocalDate.now(), discountBreakdown);

            // Validate payment details
            PaymentDetails paymentDetails = order.getPaymentDetails();
            validatePaymentDetails(paymentDetails, discountedPrice);

            // Process payments
            paymentProcessor.processPayments(paymentDetails, order.getCustomer(), discountedPrice);

            // Return the DiscountedOrder object
            return new DiscountedOrder(order, discountedPrice, discountBreakdown);

        } catch (PaymentProcessingException e) {
            // Log and rethrow with additional context
            logger.error("Payment processing failed for customer: {}. Reason: {}", order.getCustomer().getName(), e.getMessage());
            throw e;

        } catch (OrderProcessingException e) {
            // Log and rethrow with additional context
            logger.error("Order processing failed for customer: {}. Reason: {}", order.getCustomer().getName(), e.getMessage());
            throw e;

        } catch (Exception e) {
            // Log unexpected exceptions with full stack trace
            logger.error("Unexpected error occurred while placing order for customer: {}. Reason: {}", order.getCustomer().getName(), e.getMessage(), e);
            throw new OrderProcessingException("Unexpected error occurred while placing order.", e);
        }
    }
}