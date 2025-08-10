package com.raju.codekatas.refactoring.statemachine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,
    PENDING_APPROVAL,
    PAYMENT_FAILED,
    PAID,
    SHIPPED, DELIVERED, CANCELLED, RETURN_PROCESSING, REFUNDED, PENDING_BANK_CONFIRMATION
}

enum PaymentMethod {
    CREDIT_CARD,
    BANK_TRANSFER,
    WALLET
}

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

class Order {
    private String orderId;
    private OrderStatus currentState;
    private double amount;
    private String customerId;
    private List<String> items;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    public Order(String orderId, OrderStatus currentState, double amount, String customerId, List<String> items, LocalDateTime createdAt, LocalDateTime lastUpdated) {
        this.orderId = orderId;
        this.currentState = currentState;
        this.amount = amount;
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getCurrentState() {
        return currentState;
    }

    public void setCurrentState(OrderStatus currentState) {
        this.currentState = currentState;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

public class OrderStateMachine {

    private Order order;
    private String cancellationReason;
    private String paymentMethod;
    private String shippingAddress;

    public OrderStateMachine(String orderId, double amount, String customerId) {
        this.order = new Order(orderId, OrderStatus.CREATED, amount, customerId, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
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
        if (OrderStatus.CREATED.equals(order.getCurrentState()) || OrderStatus.PENDING_PAYMENT.equals(order.getCurrentState())) {
            order.getItems().add(item);
            order.setLastUpdated(LocalDateTime.now());
            System.out.println("Item added: " + item);
        } else {
            throw new IllegalStateException("Cannot add items in state: " + order.getCurrentState());
        }
    }

    public void submitOrder() {
        if (OrderStatus.CREATED.equals(order.getCurrentState())) {
            if (order.getItems().isEmpty()) {
                throw new IllegalStateException("Cannot submit order without items");
            }
            if (order.getAmount() <= 0) {
                throw new IllegalStateException("Invalid order amount");
            }

            order.setCurrentState(OrderStatus.PENDING_PAYMENT);
            order.setLastUpdated(LocalDateTime.now());
            System.out.println("Order submitted for payment: " + order.getOrderId());

            // Business logic for order submission
            validateCustomer();
            calculateTax();
            reserveInventory();
        } else {
            throw new IllegalStateException("Cannot submit order from state: " + order.getCurrentState());
        }
    }

    public void processPayment(String paymentMethod) {
        if (OrderStatus.PENDING_PAYMENT.equals(order.getCurrentState())) {
            this.paymentMethod = paymentMethod;

            // Complex payment processing logic
            if (PaymentMethod.CREDIT_CARD.equals(PaymentMethod.valueOf(paymentMethod))) {
                if (order.getAmount() > 10000) {
                    // Requires manual approval for large amounts

                    order.setCurrentState(OrderStatus.PENDING_APPROVAL);
                    System.out.println("Large payment requires approval: " + order.getOrderId());
                } else {
                    if (processCardPayment()) {

                        order.setCurrentState(OrderStatus.PAID);
                        System.out.println("Payment processed successfully: " + order.getOrderId());
                    } else {
                        order.setCurrentState(OrderStatus.PAYMENT_FAILED);
                        System.out.println("Payment failed: " + order.getOrderId());
                    }
                }
            } else if (PaymentMethod.BANK_TRANSFER.equals(PaymentMethod.valueOf(paymentMethod))) {
                order.setCurrentState(OrderStatus.PENDING_BANK_CONFIRMATION);
                System.out.println("Waiting for bank transfer confirmation: " + order.getOrderId());
            } else if (PaymentMethod.WALLET.equals(PaymentMethod.valueOf(paymentMethod))) {
                if (processWalletPayment()) {
                    order.setCurrentState(OrderStatus.PAID);
                    System.out.println("Wallet payment processed: " + order.getOrderId());
                } else {
                    order.setCurrentState(OrderStatus.PAYMENT_FAILED);
                    System.out.println("Insufficient wallet balance: " + order.getOrderId());
                }
            } else {
                throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
            }

            order.setLastUpdated(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot process payment from state: " + order.getCurrentState());
        }
    }

    public void approvePayment() {
        if (OrderStatus.PENDING_PAYMENT.equals(order.getCurrentState())) {
            if (processCardPayment()) {
                order.setCurrentState(OrderStatus.PAID);
                System.out.println("Payment approved and processed: " + order.getOrderId());
            } else {
                order.setCurrentState(OrderStatus.PAYMENT_FAILED);
                System.out.println("Approved payment failed: " + order.getOrderId());
            }
            order.setLastUpdated(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot approve payment from state: " + order.getCurrentState());
        }
    }

    public void confirmBankTransfer() {
        if (OrderStatus.PENDING_BANK_CONFIRMATION.equals(order.getCurrentState())) {
            order.setCurrentState(OrderStatus.PAID);
            System.out.println("Bank transfer confirmed: " + order.getOrderId());
            order.setLastUpdated(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot confirm bank transfer from state: " + order.getCurrentState());
        }
    }

    public void ship(String shippingAddress) {
        if (OrderStatus.PAID.equals(order.getCurrentState())) {
            this.shippingAddress = shippingAddress;
            order.setCurrentState(OrderStatus.SHIPPED);
            System.out.println("Order shipped to: " + shippingAddress);
            order.setLastUpdated(LocalDateTime.now());

            // Shipping logic
            generateShippingLabel();
            notifyCustomer();
            updateInventory();
        } else {
            throw new IllegalStateException("Cannot ship order from state: " + order.getCurrentState());
        }
    }

    public void deliver() {
        if (OrderStatus.SHIPPED.equals(order.getCurrentState())) {
            order.setCurrentState(OrderStatus.DELIVERED);
            System.out.println("Order delivered: " + order.getOrderId());
            order.setLastUpdated(LocalDateTime.now());

            // Delivery logic
            sendDeliveryConfirmation();
            requestReview();
            processLoyaltyPoints();
        } else {
            throw new IllegalStateException("Cannot deliver order from state: " + order.getCurrentState());
        }
    }

    public void cancel(String reason) {
        if (OrderStatus.CREATED.equals(order.getCurrentState()) || OrderStatus.PENDING_PAYMENT.equals(order.getCurrentState()) ||
                OrderStatus.PENDING_APPROVAL.equals(order.getCurrentState()) || OrderStatus.PAYMENT_FAILED.equals(order.getCurrentState())) {

            this.cancellationReason = reason;
            order.setCurrentState(OrderStatus.CANCELLED);
            System.out.println("Order cancelled: " + order.getOrderId() + ", Reason: " + reason);
            order.setLastUpdated(LocalDateTime.now());

            // Cancellation logic
            releaseInventory();
            if (OrderStatus.PENDING_APPROVAL.equals(order.getCurrentState())) {
                reversePendingPayment();
            }
            notifyCustomerCancellation();
        } else if (OrderStatus.PAID.equals(order.getCurrentState()) || OrderStatus.SHIPPED.equals(order.getCurrentState())) {
            // Can only refund, not cancel
            refund(reason);
        } else {
            throw new IllegalStateException("Cannot cancel order from state: " + order.getCurrentState());
        }
    }

    public void refund(String reason) {
        if (OrderStatus.PAID.equals(order.getCurrentState()) || OrderStatus.SHIPPED.equals(order.getCurrentState()) || OrderStatus.DELIVERED.equals(order.getCurrentState())) {
            this.cancellationReason = reason;

            if (OrderStatus.DELIVERED.equals(order.getCurrentState())) {
                // Delivered orders need return processing
                order.setCurrentState(OrderStatus.RETURN_PROCESSING);
                System.out.println("Processing return for delivered order: " + order.getOrderId());
            } else {
                order.setCurrentState(OrderStatus.REFUNDED);
                System.out.println("Order refunded: " + order.getOrderId() + ", Reason: " + reason);

                // Refund logic
                processRefundPayment();
                if (OrderStatus.SHIPPED.equals(order.getCurrentState())) {
                    arrangeReturnShipping();
                }
                restoreInventory();
            }
            order.setLastUpdated(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot refund order from state: " + order.getCurrentState());
        }
    }

    public void completeReturn() {
        if (OrderStatus.RETURN_PROCESSING.equals(order.getCurrentState())) {
            order.setCurrentState(OrderStatus.REFUNDED);
            System.out.println("Return completed and refunded: " + order.getOrderId());

            order.setLastUpdated(LocalDateTime.now());

            processRefundPayment();
            restoreInventory();
        } else {
            throw new IllegalStateException("Cannot complete return from state: " + order.getCurrentState());
        }
    }

    // Helper methods with business logic
    private void validateCustomer() {
        System.out.println("Validating customer: " + order.getCustomerId());
        // Simulate validation
    }

    private void calculateTax() {
        System.out.println("Calculating tax for order: " + order.getOrderId());
        // Simulate tax calculation
    }

    private void reserveInventory() {
        System.out.println("Reserving inventory for items: " + order.getItems());
        // Simulate inventory reservation
    }

    private boolean processCardPayment() {
        System.out.println("Processing credit card payment: " + order.getAmount());
        // Simulate payment processing - 90% success rate
        return Math.random() > 0.1;
    }

    private boolean processWalletPayment() {
        System.out.println("Processing wallet payment: " + order.getAmount());
        // Simulate wallet payment - 80% success rate (insufficient balance)
        return Math.random() > 0.2;
    }

    private void generateShippingLabel() {
        System.out.println("Generating shipping label for: " + shippingAddress);
    }

    private void notifyCustomer() {
        System.out.println("Notifying customer of shipment: " + order.getCustomerId());
    }

    private void updateInventory() {
        System.out.println("Updating inventory after shipment");
    }

    private void sendDeliveryConfirmation() {
        System.out.println("Sending delivery confirmation to: " + order.getCustomerId());
    }

    private void requestReview() {
        System.out.println("Requesting customer review for order: " + order.getOrderId());
    }

    private void processLoyaltyPoints() {
        System.out.println("Processing loyalty points for customer: " + order.getCustomerId());
    }

    private void releaseInventory() {
        System.out.println("Releasing reserved inventory");
    }

    private void reversePendingPayment() {
        System.out.println("Reversing pending payment authorization");
    }

    private void notifyCustomerCancellation() {
        System.out.println("Notifying customer of cancellation: " + order.getCustomerId());
    }

    private void processRefundPayment() {
        System.out.println("Processing refund payment: " + order.getAmount());
    }

    private void arrangeReturnShipping() {
        System.out.println("Arranging return shipping for order: " + order.getOrderId());
    }

    private void restoreInventory() {
        System.out.println("Restoring inventory for returned items");
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

