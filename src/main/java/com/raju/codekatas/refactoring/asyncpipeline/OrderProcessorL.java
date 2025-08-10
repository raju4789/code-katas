package com.raju.codekatas.refactoring.asyncpipeline;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LEGACY CODE - Blocking order processing pipeline with poor error handling
 * 
 * REFACTORING CHALLENGE:
 * Convert this blocking pipeline into an async, non-blocking processing system.
 * 
 * TIME LIMIT: 40 minutes
 * 
 * REQUIREMENTS:
 * 1. Use CompletableFuture for async processing
 * 2. Process multiple orders concurrently
 * 3. Handle failures gracefully with retries
 * 4. Implement timeout handling
 * 5. Provide progress tracking
 */
public class OrderProcessorL {
    
    public List<OrderResult> processOrders(List<Order> orders) {
        System.out.println("Processing " + orders.size() + " orders synchronously...");
        
        List<OrderResult> results = new ArrayList<>();
        
        for (Order order : orders) {
            try {
                OrderResult result = processOrder(order);
                results.add(result);
            } catch (Exception e) {
                System.err.println("Failed to process order " + order.getId() + ": " + e.getMessage());
                results.add(new OrderResult(order.getId(), "FAILED", e.getMessage(), null));
            }
        }
        
        return results;
    }
    
    public OrderResult processOrder(Order order) {
        System.out.println("Processing order: " + order.getId());
        
        // Step 1: Validate order
        ValidationResult validation = validateOrder(order);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Order validation failed: " + validation.getError());
        }
        
        // Step 2: Check inventory
        InventoryCheck inventory = checkInventory(order);
        if (!inventory.isAvailable()) {
            throw new IllegalStateException("Insufficient inventory for order: " + order.getId());
        }
        
        // Step 3: Process payment
        PaymentResult payment = processPayment(order);
        if (!payment.isSuccessful()) {
            throw new RuntimeException("Payment failed: " + payment.getErrorMessage());
        }
        
        // Step 4: Reserve inventory
        reserveInventory(order);
        
        // Step 5: Create shipment
        ShipmentInfo shipment = createShipment(order);
        
        // Step 6: Send confirmation
        sendConfirmation(order, payment.getTransactionId(), shipment.getTrackingNumber());
        
        return new OrderResult(order.getId(), "SUCCESS", null, 
            new ProcessedOrder(order, payment.getTransactionId(), shipment.getTrackingNumber()));
    }
    
    private ValidationResult validateOrder(Order order) {
        System.out.println("Validating order: " + order.getId());
        
        // Simulate validation time
        simulateProcessingTime(200, 500);
        
        if (order.getItems().isEmpty()) {
            return new ValidationResult(false, "Order must contain at least one item");
        }
        
        if (order.getTotalAmount() <= 0) {
            return new ValidationResult(false, "Order total must be positive");
        }
        
        if (order.getCustomerId() == null || order.getCustomerId().isEmpty()) {
            return new ValidationResult(false, "Customer ID is required");
        }
        
        // Random validation failure
        if (ThreadLocalRandom.current().nextDouble() < 0.05) {
            return new ValidationResult(false, "Random validation failure");
        }
        
        return new ValidationResult(true, null);
    }
    
    private InventoryCheck checkInventory(Order order) {
        System.out.println("Checking inventory for order: " + order.getId());
        
        // Simulate inventory check time
        simulateProcessingTime(300, 800);
        
        // Simulate inventory failures
        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            return new InventoryCheck(false, "Inventory service unavailable");
        }
        
        // Simulate insufficient inventory
        if (ThreadLocalRandom.current().nextDouble() < 0.15) {
            return new InventoryCheck(false, "Insufficient stock for some items");
        }
        
        return new InventoryCheck(true, null);
    }
    
    private PaymentResult processPayment(Order order) {
        System.out.println("Processing payment for order: " + order.getId());
        
        // Simulate payment processing time
        simulateProcessingTime(1000, 3000);
        
        // Simulate payment failures
        if (ThreadLocalRandom.current().nextDouble() < 0.08) {
            return new PaymentResult(false, null, "Payment gateway timeout");
        }
        
        if (ThreadLocalRandom.current().nextDouble() < 0.12) {
            return new PaymentResult(false, null, "Insufficient funds");
        }
        
        String transactionId = "TXN-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, null);
    }
    
    private void reserveInventory(Order order) {
        System.out.println("Reserving inventory for order: " + order.getId());
        
        // Simulate inventory reservation
        simulateProcessingTime(400, 700);
        
        // Simulate reservation failures
        if (ThreadLocalRandom.current().nextDouble() < 0.05) {
            throw new RuntimeException("Failed to reserve inventory");
        }
    }
    
    private ShipmentInfo createShipment(Order order) {
        System.out.println("Creating shipment for order: " + order.getId());
        
        // Simulate shipment creation
        simulateProcessingTime(500, 1000);
        
        // Simulate shipment failures
        if (ThreadLocalRandom.current().nextDouble() < 0.03) {
            throw new RuntimeException("Failed to create shipment");
        }
        
        String trackingNumber = "TRACK-" + System.currentTimeMillis();
        return new ShipmentInfo(trackingNumber, LocalDateTime.now().plusDays(3));
    }
    
    private void sendConfirmation(Order order, String transactionId, String trackingNumber) {
        System.out.println("Sending confirmation for order: " + order.getId());
        
        // Simulate email sending
        simulateProcessingTime(200, 400);
        
        // Simulate email failures (non-critical)
        if (ThreadLocalRandom.current().nextDouble() < 0.05) {
            System.err.println("Failed to send confirmation email for order: " + order.getId());
        }
    }
    
    private void simulateProcessingTime(int minMs, int maxMs) {
        try {
            int delay = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }
    
    // Data classes
    public static class Order {
        private final String id;
        private final String customerId;
        private final List<String> items;
        private final double totalAmount;
        private final LocalDateTime createdAt;
        
        public Order(String id, String customerId, List<String> items, double totalAmount) {
            this.id = id;
            this.customerId = customerId;
            this.items = new ArrayList<>(items);
            this.totalAmount = totalAmount;
            this.createdAt = LocalDateTime.now();
        }
        
        public String getId() { return id; }
        public String getCustomerId() { return customerId; }
        public List<String> getItems() { return new ArrayList<>(items); }
        public double getTotalAmount() { return totalAmount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    public static class OrderResult {
        private final String orderId;
        private final String status;
        private final String errorMessage;
        private final ProcessedOrder processedOrder;
        
        public OrderResult(String orderId, String status, String errorMessage, ProcessedOrder processedOrder) {
            this.orderId = orderId;
            this.status = status;
            this.errorMessage = errorMessage;
            this.processedOrder = processedOrder;
        }
        
        public String getOrderId() { return orderId; }
        public String getStatus() { return status; }
        public String getErrorMessage() { return errorMessage; }
        public ProcessedOrder getProcessedOrder() { return processedOrder; }
    }
    
    public static class ProcessedOrder {
        private final Order originalOrder;
        private final String transactionId;
        private final String trackingNumber;
        
        public ProcessedOrder(Order originalOrder, String transactionId, String trackingNumber) {
            this.originalOrder = originalOrder;
            this.transactionId = transactionId;
            this.trackingNumber = trackingNumber;
        }
        
        public Order getOriginalOrder() { return originalOrder; }
        public String getTransactionId() { return transactionId; }
        public String getTrackingNumber() { return trackingNumber; }
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String error;
        
        public ValidationResult(boolean valid, String error) {
            this.valid = valid;
            this.error = error;
        }
        
        public boolean isValid() { return valid; }
        public String getError() { return error; }
    }
    
    public static class InventoryCheck {
        private final boolean available;
        private final String error;
        
        public InventoryCheck(boolean available, String error) {
            this.available = available;
            this.error = error;
        }
        
        public boolean isAvailable() { return available; }
        public String getError() { return error; }
    }
    
    public static class PaymentResult {
        private final boolean successful;
        private final String transactionId;
        private final String errorMessage;
        
        public PaymentResult(boolean successful, String transactionId, String errorMessage) {
            this.successful = successful;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccessful() { return successful; }
        public String getTransactionId() { return transactionId; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class ShipmentInfo {
        private final String trackingNumber;
        private final LocalDateTime estimatedDelivery;
        
        public ShipmentInfo(String trackingNumber, LocalDateTime estimatedDelivery) {
            this.trackingNumber = trackingNumber;
            this.estimatedDelivery = estimatedDelivery;
        }
        
        public String getTrackingNumber() { return trackingNumber; }
        public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    }
    
    public static void main(String[] args) {
        OrderProcessorL processor = new OrderProcessorL();
        
        // Create test orders
        List<Order> orders = List.of(
            new Order("ORD-001", "CUST-001", List.of("Laptop", "Mouse"), 1200.00),
            new Order("ORD-002", "CUST-002", List.of("Phone", "Case"), 800.00),
            new Order("ORD-003", "CUST-003", List.of("Tablet"), 500.00)
        );
        
        long startTime = System.currentTimeMillis();
        
        // Process orders synchronously (blocking)
        List<OrderResult> results = processor.processOrders(orders);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("\n=== Results ===");
        for (OrderResult result : results) {
            System.out.println("Order " + result.getOrderId() + ": " + result.getStatus());
            if (result.getErrorMessage() != null) {
                System.out.println("  Error: " + result.getErrorMessage());
            }
        }
        
        System.out.println("\nTotal processing time: " + (endTime - startTime) + "ms");
    }
}
