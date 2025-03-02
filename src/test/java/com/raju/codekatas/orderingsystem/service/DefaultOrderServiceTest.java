package com.raju.codekatas.orderingsystem.service;


import com.raju.codekatas.orderingsystem.discount.PriceDecorator;
import com.raju.codekatas.orderingsystem.exception.OrderProcessingException;
import com.raju.codekatas.orderingsystem.exception.PaymentProcessingException;
import com.raju.codekatas.orderingsystem.model.Customer;
import com.raju.codekatas.orderingsystem.model.DiscountedOrder;
import com.raju.codekatas.orderingsystem.model.Order;
import com.raju.codekatas.orderingsystem.model.PaymentDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultOrderServiceTest {

    @Mock
    private PaymentProcessor paymentProcessor;

    @Mock
    private PriceDecorator weekendDiscount;

    @Mock
    private PriceDecorator ageDiscount;

    @InjectMocks
    private DefaultOrderService orderService;

    private Customer customer;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize customer and order
        customer = new Customer("John Doe", 100.0f, 55, LocalDate.of(1968, 10, 15));
        order = new Order(customer);
        order.setTotalPrice(100.0f);

        // Initialize discount decorators
        List<PriceDecorator> discountDecorators = new ArrayList<>();
        discountDecorators.add(weekendDiscount);
        discountDecorators.add(ageDiscount);

        // Inject the mock decorators into the service
        orderService = new DefaultOrderService(paymentProcessor, discountDecorators);
    }

    @Test
    void testPlaceOrder_Successful() {
        // Mock discounts
        when(weekendDiscount.calculatePrice(100.0f, customer, LocalDate.now())).thenReturn(90.0f); // 10% discount
        when(ageDiscount.calculatePrice(90.0f, customer, LocalDate.now())).thenReturn(85.5f); // 5% discount

        // Mock payment processing
        PaymentDetails paymentDetails = new PaymentDetails(customer.getPaymentMethods(), List.of(50.0f, 35.5f));
        order.setPaymentDetails(paymentDetails);
        doNothing().when(paymentProcessor).processPayments(paymentDetails, customer, 85.5f);

        // Place the order
        DiscountedOrder discountedOrder = orderService.placeOrder(order);

        // Verify results
        assertNotNull(discountedOrder);
        assertEquals(85.5f, discountedOrder.discountedPrice());
        assertEquals(2, discountedOrder.discountBreakdown().size());
        assertTrue(discountedOrder.formatDiscountBreakdown().contains("applied: 10.0"));
        assertTrue(discountedOrder.formatDiscountBreakdown().contains("applied: 4.5"));

        // Verify payment processing
        verify(paymentProcessor, times(1)).processPayments(paymentDetails, customer, 85.5f);
    }

    @Test
    void testPlaceOrder_InvalidSplitPayment() {
        // Mock discounts
        when(weekendDiscount.calculatePrice(100.0f, customer, LocalDate.now())).thenReturn(90.0f);
        when(ageDiscount.calculatePrice(90.0f, customer, LocalDate.now())).thenReturn(85.5f);

        // Invalid payment details (total does not match discounted price)
        PaymentDetails paymentDetails = new PaymentDetails(customer.getPaymentMethods(), List.of(50.0f, 30.0f));
        order.setPaymentDetails(paymentDetails);

        // Expect exception
        OrderProcessingException exception = assertThrows(OrderProcessingException.class, () -> {
            orderService.placeOrder(order);
        });

        assertEquals("Split payment amounts do not match the order total.", exception.getMessage());
        verify(paymentProcessor, never()).processPayments(any(), any(), any());
    }

    @Test
    void testPlaceOrder_NoDiscounts() {
        // Mock discounts (no discounts applied)
        when(weekendDiscount.calculatePrice(100.0f, customer, LocalDate.now())).thenReturn(100.0f);
        when(ageDiscount.calculatePrice(100.0f, customer, LocalDate.now())).thenReturn(100.0f);

        // Mock payment processing
        PaymentDetails paymentDetails = new PaymentDetails(customer.getPaymentMethods(), List.of(50.0f, 50.0f));
        order.setPaymentDetails(paymentDetails);
        doNothing().when(paymentProcessor).processPayments(paymentDetails, customer, 100.0f);

        // Place the order
        DiscountedOrder discountedOrder = orderService.placeOrder(order);

        // Verify results
        assertNotNull(discountedOrder);
        assertEquals(100.0f, discountedOrder.discountedPrice());
        assertTrue(discountedOrder.discountBreakdown().isEmpty());

        // Verify payment processing
        verify(paymentProcessor, times(1)).processPayments(paymentDetails, customer, 100.0f);
    }

    @Test
    void testPlaceOrder_PaymentProcessingFailure() {
        // Mock discounts
        when(weekendDiscount.calculatePrice(100.0f, customer, LocalDate.now())).thenReturn(90.0f);
        when(ageDiscount.calculatePrice(90.0f, customer, LocalDate.now())).thenReturn(85.5f);

        // Mock payment processing failure
        PaymentDetails paymentDetails = new PaymentDetails(customer.getPaymentMethods(), List.of(50.0f, 35.5f));
        order.setPaymentDetails(paymentDetails);
        doThrow(new PaymentProcessingException("Payment failed.")).when(paymentProcessor).processPayments(paymentDetails, customer, 85.5f);

        // Expect exception
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            orderService.placeOrder(order);
        });

        assertEquals("Payment failed.", exception.getMessage());
        verify(paymentProcessor, times(1)).processPayments(paymentDetails, customer, 85.5f);
    }

    @Test
    void testPlaceOrder_UnexpectedException() {
        // Mock discounts
        when(weekendDiscount.calculatePrice(100.0f, customer, LocalDate.now())).thenReturn(90.0f);
        when(ageDiscount.calculatePrice(90.0f, customer, LocalDate.now())).thenReturn(85.5f);

        // Mock unexpected exception
        PaymentDetails paymentDetails = new PaymentDetails(customer.getPaymentMethods(), List.of(50.0f, 35.5f));
        order.setPaymentDetails(paymentDetails);
        doThrow(new RuntimeException("Unexpected error")).when(paymentProcessor).processPayments(paymentDetails, customer, 85.5f);

        // Expect exception
        OrderProcessingException exception = assertThrows(OrderProcessingException.class, () -> {
            orderService.placeOrder(order);
        });

        assertEquals("Unexpected error occurred while placing order.", exception.getMessage());
        verify(paymentProcessor, times(1)).processPayments(paymentDetails, customer, 85.5f);
    }

    @Test
    void testPlaceOrder_EmptyDiscounts() {
        // Initialize service with no discounts
        orderService = new DefaultOrderService(paymentProcessor, new ArrayList<>());

        // Mock payment processing
        PaymentDetails paymentDetails = new PaymentDetails(customer.getPaymentMethods(), List.of(50.0f, 50.0f));
        order.setPaymentDetails(paymentDetails);
        doNothing().when(paymentProcessor).processPayments(paymentDetails, customer, 100.0f);

        // Place the order
        DiscountedOrder discountedOrder = orderService.placeOrder(order);

        // Verify results
        assertNotNull(discountedOrder);
        assertEquals(100.0f, discountedOrder.discountedPrice());
        assertTrue(discountedOrder.discountBreakdown().isEmpty());

        // Verify payment processing
        verify(paymentProcessor, times(1)).processPayments(paymentDetails, customer, 100.0f);
    }
}
