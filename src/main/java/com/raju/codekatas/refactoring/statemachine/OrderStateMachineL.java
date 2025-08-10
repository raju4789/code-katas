package com.raju.codekatas.refactoring.statemachine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LEGACY CODE - Complex state management with nested conditionals
 * <p>
 * REFACTORING CHALLENGE:
 * Convert this monolithic state machine into a clean State Pattern implementation.
 * <p>
 * TIME LIMIT: 40 minutes 3:40 pm
 * <p>
 * REQUIREMENTS:
 * 1. Implement State Pattern with proper state transitions
 * 2. Each state should handle its own transitions and actions
 * 3. Add validation for invalid state transitions
 * 4. Make it easy to add new states without modifying existing code
 * 5. Include proper error handling and logging
 */
public class OrderStateMachineL {

    private String orderId;
    private String currentState;
    private double amount;
    private String customerId;
    private List<String> items;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private String cancellationReason;
    private String paymentMethod;
    private String shippingAddress;

    public OrderStateMachineL(String orderId, double amount, String customerId) {
        this.orderId = orderId;
        this.amount = amount;
        this.customerId = customerId;
        this.currentState = "CREATED";
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public static void main(String[] args) {
        // Demo of the complex state machine
        OrderStateMachineL order = new OrderStateMachineL("ORD-001", 5000.0, "CUST-123");

        try {
            order.addItem("Laptop");
            order.addItem("Mouse");
            order.submitOrder();
            order.processPayment("CREDIT_CARD");
            order.ship("123 Main St, City, State");
            order.deliver();

            System.out.println("Final state: " + order.getCurrentState());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void addItem(String item) {
        if ("CREATED".equals(currentState) || "PENDING_PAYMENT".equals(currentState)) {
            items.add(item);
            this.lastUpdated = LocalDateTime.now();
            System.out.println("Item added: " + item);
        } else {
            throw new IllegalStateException("Cannot add items in state: " + currentState);
        }
    }

    public void submitOrder() {
        if ("CREATED".equals(currentState)) {
            if (items.isEmpty()) {
                throw new IllegalStateException("Cannot submit order without items");
            }
            if (amount <= 0) {
                throw new IllegalStateException("Invalid order amount");
            }
            currentState = "PENDING_PAYMENT";
            this.lastUpdated = LocalDateTime.now();
            System.out.println("Order submitted for payment: " + orderId);

            // Business logic for order submission
            validateCustomer();
            calculateTax();
            reserveInventory();
        } else {
            throw new IllegalStateException("Cannot submit order from state: " + currentState);
        }
    }

    public void processPayment(String paymentMethod) {
        if ("PENDING_PAYMENT".equals(currentState)) {
            this.paymentMethod = paymentMethod;

            // Complex payment processing logic
            if ("CREDIT_CARD".equals(paymentMethod)) {
                if (amount > 10000) {
                    // Requires manual approval for large amounts
                    currentState = "PENDING_APPROVAL";
                    System.out.println("Large payment requires approval: " + orderId);
                } else {
                    if (processCardPayment()) {
                        currentState = "PAID";
                        System.out.println("Payment processed successfully: " + orderId);
                    } else {
                        currentState = "PAYMENT_FAILED";
                        System.out.println("Payment failed: " + orderId);
                    }
                }
            } else if ("BANK_TRANSFER".equals(paymentMethod)) {
                currentState = "PENDING_BANK_CONFIRMATION";
                System.out.println("Waiting for bank transfer confirmation: " + orderId);
            } else if ("WALLET".equals(paymentMethod)) {
                if (processWalletPayment()) {
                    currentState = "PAID";
                    System.out.println("Wallet payment processed: " + orderId);
                } else {
                    currentState = "PAYMENT_FAILED";
                    System.out.println("Insufficient wallet balance: " + orderId);
                }
            } else {
                throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
            }
            this.lastUpdated = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot process payment from state: " + currentState);
        }
    }

    public void approvePayment() {
        if ("PENDING_APPROVAL".equals(currentState)) {
            if (processCardPayment()) {
                currentState = "PAID";
                System.out.println("Payment approved and processed: " + orderId);
            } else {
                currentState = "PAYMENT_FAILED";
                System.out.println("Approved payment failed: " + orderId);
            }
            this.lastUpdated = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot approve payment from state: " + currentState);
        }
    }

    public void confirmBankTransfer() {
        if ("PENDING_BANK_CONFIRMATION".equals(currentState)) {
            currentState = "PAID";
            System.out.println("Bank transfer confirmed: " + orderId);
            this.lastUpdated = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot confirm bank transfer from state: " + currentState);
        }
    }

    public void ship(String shippingAddress) {
        if ("PAID".equals(currentState)) {
            this.shippingAddress = shippingAddress;
            currentState = "SHIPPED";
            System.out.println("Order shipped to: " + shippingAddress);
            this.lastUpdated = LocalDateTime.now();

            // Shipping logic
            generateShippingLabel();
            notifyCustomer();
            updateInventory();
        } else {
            throw new IllegalStateException("Cannot ship order from state: " + currentState);
        }
    }

    public void deliver() {
        if ("SHIPPED".equals(currentState)) {
            currentState = "DELIVERED";
            System.out.println("Order delivered: " + orderId);
            this.lastUpdated = LocalDateTime.now();

            // Delivery logic
            sendDeliveryConfirmation();
            requestReview();
            processLoyaltyPoints();
        } else {
            throw new IllegalStateException("Cannot deliver order from state: " + currentState);
        }
    }

    public void cancel(String reason) {
        if ("CREATED".equals(currentState) || "PENDING_PAYMENT".equals(currentState) ||
                "PENDING_APPROVAL".equals(currentState) || "PAYMENT_FAILED".equals(currentState)) {

            this.cancellationReason = reason;
            currentState = "CANCELLED";
            System.out.println("Order cancelled: " + orderId + ", Reason: " + reason);
            this.lastUpdated = LocalDateTime.now();

            // Cancellation logic
            releaseInventory();
            if ("PENDING_APPROVAL".equals(currentState)) {
                reversePendingPayment();
            }
            notifyCustomerCancellation();
        } else if ("PAID".equals(currentState) || "SHIPPED".equals(currentState)) {
            // Can only refund, not cancel
            refund(reason);
        } else {
            throw new IllegalStateException("Cannot cancel order from state: " + currentState);
        }
    }

    public void refund(String reason) {
        if ("PAID".equals(currentState) || "SHIPPED".equals(currentState) || "DELIVERED".equals(currentState)) {
            this.cancellationReason = reason;

            if ("DELIVERED".equals(currentState)) {
                // Delivered orders need return processing
                currentState = "RETURN_PROCESSING";
                System.out.println("Processing return for delivered order: " + orderId);
            } else {
                currentState = "REFUNDED";
                System.out.println("Order refunded: " + orderId + ", Reason: " + reason);

                // Refund logic
                processRefundPayment();
                if ("SHIPPED".equals(currentState)) {
                    arrangeReturnShipping();
                }
                restoreInventory();
            }
            this.lastUpdated = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot refund order from state: " + currentState);
        }
    }

    public void completeReturn() {
        if ("RETURN_PROCESSING".equals(currentState)) {
            currentState = "REFUNDED";
            System.out.println("Return completed and refunded: " + orderId);
            this.lastUpdated = LocalDateTime.now();

            processRefundPayment();
            restoreInventory();
        } else {
            throw new IllegalStateException("Cannot complete return from state: " + currentState);
        }
    }

    // Helper methods with business logic
    private void validateCustomer() {
        System.out.println("Validating customer: " + customerId);
        // Simulate validation
    }

    private void calculateTax() {
        System.out.println("Calculating tax for order: " + orderId);
        // Simulate tax calculation
    }

    private void reserveInventory() {
        System.out.println("Reserving inventory for items: " + items);
        // Simulate inventory reservation
    }

    private boolean processCardPayment() {
        System.out.println("Processing credit card payment: " + amount);
        // Simulate payment processing - 90% success rate
        return Math.random() > 0.1;
    }

    private boolean processWalletPayment() {
        System.out.println("Processing wallet payment: " + amount);
        // Simulate wallet payment - 80% success rate (insufficient balance)
        return Math.random() > 0.2;
    }

    private void generateShippingLabel() {
        System.out.println("Generating shipping label for: " + shippingAddress);
    }

    private void notifyCustomer() {
        System.out.println("Notifying customer of shipment: " + customerId);
    }

    private void updateInventory() {
        System.out.println("Updating inventory after shipment");
    }

    private void sendDeliveryConfirmation() {
        System.out.println("Sending delivery confirmation to: " + customerId);
    }

    private void requestReview() {
        System.out.println("Requesting customer review for order: " + orderId);
    }

    private void processLoyaltyPoints() {
        System.out.println("Processing loyalty points for customer: " + customerId);
    }

    private void releaseInventory() {
        System.out.println("Releasing reserved inventory");
    }

    private void reversePendingPayment() {
        System.out.println("Reversing pending payment authorization");
    }

    private void notifyCustomerCancellation() {
        System.out.println("Notifying customer of cancellation: " + customerId);
    }

    private void processRefundPayment() {
        System.out.println("Processing refund payment: " + amount);
    }

    private void arrangeReturnShipping() {
        System.out.println("Arranging return shipping for order: " + orderId);
    }

    private void restoreInventory() {
        System.out.println("Restoring inventory for returned items");
    }

    // Getters
    public String getCurrentState() {
        return currentState;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<String> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }
}
