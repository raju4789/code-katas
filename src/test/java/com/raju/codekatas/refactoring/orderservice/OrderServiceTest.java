package com.raju.codekatas.refactoring.orderservice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private final PersistentService persistentService = new DBService();

    private final Map<PaymentType, PaymentHandler> paymentHandlerMap = Map.of(
            PaymentType.CREDIT_CARD, new CreditCardHandler(),
            PaymentType.PAYPAL, new PaypalHandler(),
            PaymentType.BITCOIN, new BitcoinHandler()

    );

    @Test
    public void testValidOrderProcessing() {
        OrderService service = new OrderService(persistentService, paymentHandlerMap);

        Order order = new Order();
        order.setItems(Arrays.asList(
                new Item("item1", 10, 2),
                new Item("item2", 5, 1)
        ));
        order.setDiscountApplied(false);

        Email email = new Email("user@example.com");
        PhoneNumber phone = new PhoneNumber("1234567890");

        assertDoesNotThrow(() -> {
            service.processOrder(order, "CREDIT_CARD", email, phone);
        });
    }

    @Test
    public void testInvalidEmail() {

        OrderService service = new OrderService(persistentService, paymentHandlerMap);

        Order order = new Order();
        order.setItems(Arrays.asList(
                new Item("item1", 10, 2),
                new Item("item2", 5, 1)
        ));
        order.setDiscountApplied(false);

        Email email = new Email("invalidemail");
        PhoneNumber phone = new PhoneNumber("1234567890");

        assertThrows(IllegalArgumentException.class, () -> service.processOrder(order, "CREDIT_CARD", email, phone));
    }

    @Test
    public void testInvalidPhoneNumber() {

        OrderService service = new OrderService(persistentService, paymentHandlerMap);

        Order order = new Order();
        order.setItems(Arrays.asList(
                new Item("item1", 10, 2),
                new Item("item2", 5, 1)
        ));
        order.setDiscountApplied(false);

        Email email = new Email("user@example.com");
        PhoneNumber phone = new PhoneNumber("12345");

        assertThrows(IllegalArgumentException.class, () -> service.processOrder(order, "CREDIT_CARD", email, phone));
    }

    @Test
    public void testOrderWithNoItemsThrows() {

        OrderService service = new OrderService(persistentService, paymentHandlerMap);
        Order order = new Order();
        order.setItems(null);

        Email email = new Email("user@example.com");
        PhoneNumber phone = new PhoneNumber("1234567890");

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            service.processOrder(order, "PAYPAL", email, phone);
        });
        assertEquals("Order has no items", e.getMessage());
    }

    @Test
    public void testUnknownPaymentTypeThrows() {
        OrderService service = new OrderService(persistentService, paymentHandlerMap);

        Order order = new Order();
        order.setItems(List.of(new Item("item1", 10, 1)));
        order.setDiscountApplied(false);

        Email email = new Email("user@example.com");
        PhoneNumber phone = new PhoneNumber("1234567890");

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            service.processOrder(order, "UNKNOWN", email, phone);
        });
        assertEquals("Invalid payment type: UNKNOWN", e.getMessage());
    }
}

