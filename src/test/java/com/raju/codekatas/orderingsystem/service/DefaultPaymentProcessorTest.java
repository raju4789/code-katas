package com.raju.codekatas.orderingsystem.service;

import com.raju.codekatas.orderingsystem.exception.PaymentProcessingException;
import com.raju.codekatas.orderingsystem.model.Customer;
import com.raju.codekatas.orderingsystem.model.PaymentDetails;
import com.raju.codekatas.orderingsystem.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPaymentProcessorTest {

    private DefaultPaymentProcessor paymentProcessor;

    @Mock
    private PaymentMethod walletPayment;

    @Mock
    private PaymentMethod creditCardPayment;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize the payment processor
        paymentProcessor = new DefaultPaymentProcessor();

        // Initialize a customer
        customer = new Customer("John Doe", 100.0f, 55, LocalDate.of(1968, 10, 15));
    }

    @Test
    void testProcessPayments_Successful() {
        // Mock payment methods
        when(walletPayment.pay(30.0f, customer)).thenReturn(true);
        when(creditCardPayment.pay(20.0f, customer)).thenReturn(true);

        // Create payment details
        PaymentDetails paymentDetails = new PaymentDetails(List.of(walletPayment, creditCardPayment), List.of(30.0f, 20.0f));

        // Process payments
        assertDoesNotThrow(() -> paymentProcessor.processPayments(paymentDetails, customer, 50.0f));

        // Verify payment methods were called
        verify(walletPayment, times(1)).pay(30.0f, customer);
        verify(creditCardPayment, times(1)).pay(20.0f, customer);
    }

    @Test
    void testProcessPayments_ValidationError() {
        // Create payment details with mismatched total
        PaymentDetails paymentDetails = new PaymentDetails(List.of(walletPayment, creditCardPayment), List.of(30.0f, 20.0f));

        // Expect exception
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentProcessor.processPayments(paymentDetails, customer, 60.0f); // Order total is 60, but split total is 50
        });

        assertEquals("Split payment amounts do not match the order total.", exception.getMessage());

        // Verify payment methods were not called
        verify(walletPayment, never()).pay(anyFloat(), any());
        verify(creditCardPayment, never()).pay(anyFloat(), any());
    }

    @Test
    void testProcessPayments_PaymentMethodFailure() {
        // Mock payment methods
        when(walletPayment.pay(30.0f, customer)).thenReturn(true);
        when(creditCardPayment.pay(20.0f, customer)).thenReturn(false); // Credit card payment fails

        // Create payment details
        PaymentDetails paymentDetails = new PaymentDetails(List.of(walletPayment, creditCardPayment), List.of(30.0f, 20.0f));

        // Expect exception
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentProcessor.processPayments(paymentDetails, customer, 50.0f);
        });

        assertEquals("Payment failed", exception.getMessage());

        // Verify payment methods were called
        verify(walletPayment, times(1)).pay(30.0f, customer);
        verify(creditCardPayment, times(1)).pay(20.0f, customer);
    }

    @Test
    void testProcessPayments_EmptyPaymentDetails() {
        // Create empty payment details
        PaymentDetails paymentDetails = new PaymentDetails(new ArrayList<>(), new ArrayList<>());

        // Expect exception
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentProcessor.processPayments(paymentDetails, customer, 50.0f);
        });

        assertEquals("Split payment amounts do not match the order total.", exception.getMessage());
    }

    @Test
    void testProcessPayments_UnexpectedException() {
        // Mock payment methods
        when(walletPayment.pay(30.0f, customer)).thenThrow(new RuntimeException("Unexpected error"));

        // Create payment details
        PaymentDetails paymentDetails = new PaymentDetails(List.of(walletPayment), List.of(30.0f));

        // Expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentProcessor.processPayments(paymentDetails, customer, 30.0f);
        });

        assertEquals("Unexpected error", exception.getMessage());

        // Verify payment methods were called
        verify(walletPayment, times(1)).pay(30.0f, customer);
    }
}
