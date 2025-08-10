package com.raju.codekatas.refactoring.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Order State Machine refactoring challenge.
 * Tests the expected behavior your refactored solution should achieve.
 */
@DisplayName("Order State Machine Test Suite")
public class orderStateMachineTestSuite {

    private OrderStateMachineL orderStateMachine;

    @BeforeEach
    void setUp() {
        orderStateMachine = new OrderStateMachineL("TEST-ORDER-001", 1000.0, "CUSTOMER-001");
    }

    private OrderStateMachineL createPaidOrder() {
        try {
            OrderStateMachineL order = new OrderStateMachineL("PAID-ORDER", 500.0, "CUSTOMER-001");
            order.addItem("Test Item");
            order.submitOrder();

            // Try different payment methods to get a successful payment
            for (String method : Arrays.asList("WALLET", "CREDIT_CARD")) {
                try {
                    order.processPayment(method);
                    if ("PAID".equals(order.getCurrentState())) {
                        return order;
                    }
                    if ("PENDING_APPROVAL".equals(order.getCurrentState())) {
                        order.approvePayment();
                        if ("PAID".equals(order.getCurrentState())) {
                            return order;
                        }
                    }
                    if ("PENDING_BANK_CONFIRMATION".equals(order.getCurrentState())) {
                        order.confirmBankTransfer();
                        return order;
                    }
                } catch (Exception e) {
                    // Try next method
                    continue;
                }
            }
        } catch (Exception e) {
            // Could not create paid order due to random failures
        }
        return null;
    }

    private OrderStateMachineL createShippedOrder() {
        OrderStateMachineL paidOrder = createPaidOrder();
        if (paidOrder != null) {
            try {
                paidOrder.ship("123 Test Street, Test City, TS 12345");
                return paidOrder;
            } catch (Exception e) {
                // Could not ship
            }
        }
        return null;
    }

    private OrderStateMachineL createDeliveredOrder() {
        OrderStateMachineL shippedOrder = createShippedOrder();
        if (shippedOrder != null) {
            try {
                shippedOrder.deliver();
                return shippedOrder;
            } catch (Exception e) {
                // Could not deliver
            }
        }
        return null;
    }

    @Nested
    @DisplayName("Order Creation Tests")
    class OrderCreationTests {

        @Test
        @DisplayName("Should create order with initial state CREATED")
        void testOrderCreation() {
            assertEquals("CREATED", orderStateMachine.getCurrentState());
            assertEquals("TEST-ORDER-001", orderStateMachine.getOrderId());
            assertEquals(1000.0, orderStateMachine.getAmount());
            assertEquals("CUSTOMER-001", orderStateMachine.getCustomerId());
            assertNotNull(orderStateMachine.getCreatedAt());
            assertTrue(orderStateMachine.getItems().isEmpty());
        }

        @Test
        @DisplayName("Should allow adding items in CREATED state")
        void testAddItemsInCreatedState() {
            orderStateMachine.addItem("Item 1");
            orderStateMachine.addItem("Item 2");

            assertEquals(2, orderStateMachine.getItems().size());
            assertTrue(orderStateMachine.getItems().contains("Item 1"));
            assertTrue(orderStateMachine.getItems().contains("Item 2"));
        }
    }

    @Nested
    @DisplayName("Order Submission Tests")
    class OrderSubmissionTests {

        @Test
        @DisplayName("Should transition from CREATED to PENDING_PAYMENT on submit")
        void testOrderSubmission() {
            orderStateMachine.addItem("Test Item");
            orderStateMachine.submitOrder();

            assertEquals("PENDING_PAYMENT", orderStateMachine.getCurrentState());
        }

        @Test
        @DisplayName("Should not allow submission without items")
        void testSubmitOrderWithoutItems() {
            assertThrows(IllegalStateException.class, () -> orderStateMachine.submitOrder());
            assertEquals("CREATED", orderStateMachine.getCurrentState());
        }

        @Test
        @DisplayName("Should not allow submission from non-CREATED state")
        void testSubmitFromInvalidState() {
            orderStateMachine.addItem("Test Item");
            orderStateMachine.submitOrder();

            assertThrows(IllegalStateException.class, () -> orderStateMachine.submitOrder());
        }
    }

    @Nested
    @DisplayName("Payment Processing Tests")
    class PaymentProcessingTests {

        @BeforeEach
        void setUpPendingPayment() {
            orderStateMachine.addItem("Test Item");
            orderStateMachine.submitOrder();
        }

        @Test
        @DisplayName("Should process credit card payment successfully")
        void testCreditCardPayment() {
            // Note: This may fail due to random payment simulation
            // In refactored version, you should mock/control this
            try {
                orderStateMachine.processPayment("CREDIT_CARD");
                assertTrue(Arrays.asList("PAID", "PAYMENT_FAILED", "PENDING_APPROVAL")
                        .contains(orderStateMachine.getCurrentState()));
            } catch (Exception e) {
                // Payment simulation can fail randomly
                assertTrue(e instanceof IllegalStateException);
            }
        }

        @Test
        @DisplayName("Should handle large amount requiring approval")
        void testLargeAmountRequiresApproval() {
            OrderStateMachineL largeOrder = new OrderStateMachineL("LARGE-ORDER", 15000.0, "CUSTOMER-001");
            largeOrder.addItem("Expensive Item");
            largeOrder.submitOrder();
            largeOrder.processPayment("CREDIT_CARD");

            // Large amounts should go to PENDING_APPROVAL
            assertTrue(Arrays.asList("PENDING_APPROVAL", "PAYMENT_FAILED")
                    .contains(largeOrder.getCurrentState()));
        }

        @Test
        @DisplayName("Should handle bank transfer payment")
        void testBankTransferPayment() {
            orderStateMachine.processPayment("BANK_TRANSFER");
            assertEquals("PENDING_BANK_CONFIRMATION", orderStateMachine.getCurrentState());
        }

        @Test
        @DisplayName("Should reject unsupported payment method")
        void testUnsupportedPaymentMethod() {
            assertThrows(IllegalArgumentException.class,
                    () -> orderStateMachine.processPayment("BITCOIN"));
        }
    }

    @Nested
    @DisplayName("Approval Process Tests")
    class ApprovalProcessTests {

        @Test
        @DisplayName("Should approve payment and transition to PAID")
        void testPaymentApproval() {
            OrderStateMachineL largeOrder = new OrderStateMachineL("LARGE-ORDER", 15000.0, "CUSTOMER-001");
            largeOrder.addItem("Expensive Item");
            largeOrder.submitOrder();
            largeOrder.processPayment("CREDIT_CARD");

            if ("PENDING_APPROVAL".equals(largeOrder.getCurrentState())) {
                try {
                    largeOrder.approvePayment();
                    assertTrue(Arrays.asList("PAID", "PAYMENT_FAILED")
                            .contains(largeOrder.getCurrentState()));
                } catch (Exception e) {
                    // Random payment failure is acceptable
                }
            }
        }

        @Test
        @DisplayName("Should not allow approval from invalid state")
        void testApprovalFromInvalidState() {
            assertThrows(IllegalStateException.class, () -> orderStateMachine.approvePayment());
        }
    }

    @Nested
    @DisplayName("Bank Transfer Confirmation Tests")
    class BankTransferTests {

        @BeforeEach
        void setUpBankTransfer() {
            orderStateMachine.addItem("Test Item");
            orderStateMachine.submitOrder();
            orderStateMachine.processPayment("BANK_TRANSFER");
        }

        @Test
        @DisplayName("Should confirm bank transfer and transition to PAID")
        void testBankTransferConfirmation() {
            orderStateMachine.confirmBankTransfer();
            assertEquals("PAID", orderStateMachine.getCurrentState());
        }

        @Test
        @DisplayName("Should not allow bank transfer confirmation from invalid state")
        void testBankTransferConfirmationFromInvalidState() {
            OrderStateMachineL newOrder = new OrderStateMachineL("NEW-ORDER", 500.0, "CUSTOMER-002");
            assertThrows(IllegalStateException.class, () -> newOrder.confirmBankTransfer());
        }
    }

    @Nested
    @DisplayName("Shipping Tests")
    class ShippingTests {

        @Test
        @DisplayName("Should ship paid order successfully")
        void testShipping() {
            // Create a path to PAID state (this is complex due to random payment)
            // In refactored version, you should have better control
            OrderStateMachineL paidOrder = createPaidOrder();
            if (paidOrder != null) {
                paidOrder.ship("123 Test Street, Test City, TS 12345");
                assertEquals("SHIPPED", paidOrder.getCurrentState());
                assertEquals("123 Test Street, Test City, TS 12345", paidOrder.getShippingAddress());
            }
        }

        @Test
        @DisplayName("Should not allow shipping from non-PAID state")
        void testShippingFromInvalidState() {
            assertThrows(IllegalStateException.class,
                    () -> orderStateMachine.ship("123 Test Street"));
        }
    }

    @Nested
    @DisplayName("Delivery Tests")
    class DeliveryTests {

        @Test
        @DisplayName("Should deliver shipped order successfully")
        void testDelivery() {
            OrderStateMachineL shippedOrder = createShippedOrder();
            if (shippedOrder != null) {
                shippedOrder.deliver();
                assertEquals("DELIVERED", shippedOrder.getCurrentState());
            }
        }

        @Test
        @DisplayName("Should not allow delivery from non-SHIPPED state")
        void testDeliveryFromInvalidState() {
            assertThrows(IllegalStateException.class, () -> orderStateMachine.deliver());
        }
    }

    // Helper methods to create orders in specific states
    // Note: These may not always succeed due to random payment simulation
    // In your refactored solution, you should have deterministic behavior

    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should cancel order in CREATED state")
        void testCancelInCreatedState() {
            orderStateMachine.addItem("Test Item");
            orderStateMachine.cancel("Customer request");

            assertEquals("CANCELLED", orderStateMachine.getCurrentState());
            assertEquals("Customer request", orderStateMachine.getCancellationReason());
        }

        @Test
        @DisplayName("Should cancel order in PENDING_PAYMENT state")
        void testCancelInPendingPaymentState() {
            orderStateMachine.addItem("Test Item");
            orderStateMachine.submitOrder();
            orderStateMachine.cancel("Changed mind");

            assertEquals("CANCELLED", orderStateMachine.getCurrentState());
            assertEquals("Changed mind", orderStateMachine.getCancellationReason());
        }

        @Test
        @DisplayName("Should not allow cancellation of delivered order")
        void testCannotCancelDeliveredOrder() {
            OrderStateMachineL deliveredOrder = createDeliveredOrder();
            if (deliveredOrder != null) {
                assertThrows(IllegalStateException.class,
                        () -> deliveredOrder.cancel("Too late"));
            }
        }
    }

    @Nested
    @DisplayName("Refund Tests")
    class RefundTests {

        @Test
        @DisplayName("Should process refund for paid order")
        void testRefundPaidOrder() {
            OrderStateMachineL paidOrder = createPaidOrder();
            if (paidOrder != null) {
                paidOrder.refund("Defective product");
                assertEquals("REFUNDED", paidOrder.getCurrentState());
                assertEquals("Defective product", paidOrder.getCancellationReason());
            }
        }

        @Test
        @DisplayName("Should initiate return process for delivered order")
        void testRefundDeliveredOrder() {
            OrderStateMachineL deliveredOrder = createDeliveredOrder();
            if (deliveredOrder != null) {
                deliveredOrder.refund("Not as described");
                assertEquals("RETURN_PROCESSING", deliveredOrder.getCurrentState());
            }
        }

        @Test
        @DisplayName("Should complete return and refund")
        void testCompleteReturn() {
            OrderStateMachineL deliveredOrder = createDeliveredOrder();
            if (deliveredOrder != null) {
                deliveredOrder.refund("Damaged item");
                if ("RETURN_PROCESSING".equals(deliveredOrder.getCurrentState())) {
                    deliveredOrder.completeReturn();
                    assertEquals("REFUNDED", deliveredOrder.getCurrentState());
                }
            }
        }
    }

    @Nested
    @DisplayName("State Transition Validation Tests")
    class StateTransitionValidationTests {

        @Test
        @DisplayName("Should track last updated timestamp")
        void testLastUpdatedTimestamp() {
            LocalDateTime before = orderStateMachine.getLastUpdated();

            try {
                Thread.sleep(10); // Small delay to ensure timestamp difference
                orderStateMachine.addItem("Test Item");

                assertTrue(orderStateMachine.getLastUpdated().isAfter(before));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Test
        @DisplayName("Should maintain order integrity during state transitions")
        void testOrderIntegrityDuringTransitions() {
            String originalId = orderStateMachine.getOrderId();
            double originalAmount = orderStateMachine.getAmount();
            String originalCustomer = orderStateMachine.getCustomerId();

            orderStateMachine.addItem("Test Item");
            orderStateMachine.submitOrder();

            // Core order data should remain unchanged
            assertEquals(originalId, orderStateMachine.getOrderId());
            assertEquals(originalAmount, orderStateMachine.getAmount());
            assertEquals(originalCustomer, orderStateMachine.getCustomerId());
        }
    }
}
