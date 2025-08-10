package com.raju.codekatas.refactoring.resilience;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LEGACY CODE - Payment service with no resilience patterns
 * 
 * REFACTORING CHALLENGE:
 * Add Circuit Breaker pattern and basic resilience to this fragile payment service.
 * 
 * TIME LIMIT: 40 minutes
 * 
 * REQUIREMENTS:
 * 1. Implement Circuit Breaker pattern for external payment gateway
 * 2. Add retry mechanism with exponential backoff
 * 3. Implement timeout handling
 * 4. Add fallback payment method when primary fails
 * 5. Track failure metrics and health status
 */
public class PaymentServiceL {
    
    private Map<String, Integer> failureCounts = new HashMap<>();
    private Map<String, LocalDateTime> lastFailureTimes = new HashMap<>();
    
    public PaymentResult processPayment(PaymentRequest request) {
        System.out.println("Processing payment: " + request.getPaymentId());
        
        try {
            // Direct call to external payment gateway - no resilience
            PaymentGatewayResponse response = callPaymentGateway(request);
            
            if (response.isSuccessful()) {
                return new PaymentResult(true, response.getTransactionId(), 
                    request.getAmount(), "PRIMARY_GATEWAY", null);
            } else {
                throw new RuntimeException("Payment gateway rejected: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Payment failed for " + request.getPaymentId() + ": " + e.getMessage());
            throw new RuntimeException("Payment processing failed", e);
        }
    }
    
    public PaymentResult processPaymentWithFallback(PaymentRequest request) {
        System.out.println("Processing payment with fallback: " + request.getPaymentId());
        
        try {
            // Try primary gateway first
            PaymentGatewayResponse response = callPaymentGateway(request);
            
            if (response.isSuccessful()) {
                return new PaymentResult(true, response.getTransactionId(), 
                    request.getAmount(), "PRIMARY_GATEWAY", null);
            } else {
                System.out.println("Primary gateway failed, trying fallback...");
                // Try fallback gateway
                PaymentGatewayResponse fallbackResponse = callFallbackGateway(request);
                
                if (fallbackResponse.isSuccessful()) {
                    return new PaymentResult(true, fallbackResponse.getTransactionId(), 
                        request.getAmount(), "FALLBACK_GATEWAY", "Primary gateway unavailable");
                } else {
                    throw new RuntimeException("Both payment gateways failed");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Payment processing failed completely: " + e.getMessage());
            throw new RuntimeException("All payment methods failed", e);
        }
    }
    
    public HealthStatus checkPaymentGatewayHealth() {
        System.out.println("Checking payment gateway health...");
        
        try {
            PaymentRequest healthCheck = new PaymentRequest("HEALTH_CHECK", "TEST", 1.0, "4111111111111111");
            PaymentGatewayResponse response = callPaymentGateway(healthCheck);
            
            return new HealthStatus("HEALTHY", "Payment gateway responding normally", LocalDateTime.now());
            
        } catch (Exception e) {
            return new HealthStatus("UNHEALTHY", "Payment gateway error: " + e.getMessage(), LocalDateTime.now());
        }
    }
    
    private PaymentGatewayResponse callPaymentGateway(PaymentRequest request) {
        System.out.println("Calling primary payment gateway for: " + request.getPaymentId());
        
        // Simulate network delay
        simulateNetworkDelay(500, 2000);
        
        // Simulate various failure scenarios
        double random = ThreadLocalRandom.current().nextDouble();
        
        if (random < 0.15) {
            // Simulate timeout
            simulateNetworkDelay(5000, 8000);
            throw new RuntimeException("Gateway timeout");
        }
        
        if (random < 0.25) {
            // Simulate service unavailable
            throw new RuntimeException("Payment gateway service unavailable");
        }
        
        if (random < 0.35) {
            // Simulate payment declined
            return new PaymentGatewayResponse(false, null, "Card declined");
        }
        
        if (random < 0.4) {
            // Simulate insufficient funds
            return new PaymentGatewayResponse(false, null, "Insufficient funds");
        }
        
        // Successful payment
        String transactionId = "TXN_" + System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(1000);
        return new PaymentGatewayResponse(true, transactionId, null);
    }
    
    private PaymentGatewayResponse callFallbackGateway(PaymentRequest request) {
        System.out.println("Calling fallback payment gateway for: " + request.getPaymentId());
        
        // Simulate fallback gateway delay
        simulateNetworkDelay(300, 1500);
        
        // Fallback gateway is more reliable but slower
        double random = ThreadLocalRandom.current().nextDouble();
        
        if (random < 0.08) {
            throw new RuntimeException("Fallback gateway timeout");
        }
        
        if (random < 0.15) {
            return new PaymentGatewayResponse(false, null, "Card declined by fallback gateway");
        }
        
        // Successful payment
        String transactionId = "FALLBACK_TXN_" + System.currentTimeMillis();
        return new PaymentGatewayResponse(true, transactionId, null);
    }
    
    private void simulateNetworkDelay(int minMs, int maxMs) {
        try {
            int delay = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    // Data classes
    public static class PaymentRequest {
        private final String paymentId;
        private final String customerId;
        private final double amount;
        private final String cardNumber;
        private final LocalDateTime timestamp;
        
        public PaymentRequest(String paymentId, String customerId, double amount, String cardNumber) {
            this.paymentId = paymentId;
            this.customerId = customerId;
            this.amount = amount;
            this.cardNumber = cardNumber;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getPaymentId() { return paymentId; }
        public String getCustomerId() { return customerId; }
        public double getAmount() { return amount; }
        public String getCardNumber() { return cardNumber; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static class PaymentResult {
        private final boolean successful;
        private final String transactionId;
        private final double amount;
        private final String gateway;
        private final String fallbackReason;
        
        public PaymentResult(boolean successful, String transactionId, double amount, String gateway, String fallbackReason) {
            this.successful = successful;
            this.transactionId = transactionId;
            this.amount = amount;
            this.gateway = gateway;
            this.fallbackReason = fallbackReason;
        }
        
        public boolean isSuccessful() { return successful; }
        public String getTransactionId() { return transactionId; }
        public double getAmount() { return amount; }
        public String getGateway() { return gateway; }
        public String getFallbackReason() { return fallbackReason; }
    }
    
    public static class PaymentGatewayResponse {
        private final boolean successful;
        private final String transactionId;
        private final String errorMessage;
        
        public PaymentGatewayResponse(boolean successful, String transactionId, String errorMessage) {
            this.successful = successful;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccessful() { return successful; }
        public String getTransactionId() { return transactionId; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class HealthStatus {
        private final String status;
        private final String message;
        private final LocalDateTime timestamp;
        
        public HealthStatus(String status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static void main(String[] args) {
        PaymentServiceL paymentService = new PaymentServiceL();
        
        // Test basic payment processing
        PaymentRequest request = new PaymentRequest("PAY-001", "CUST-123", 99.99, "4111111111111111");
        
        try {
            System.out.println("=== Testing Basic Payment ===");
            PaymentResult result = paymentService.processPayment(request);
            System.out.println("Payment successful: " + result.getTransactionId());
        } catch (Exception e) {
            System.err.println("Payment failed: " + e.getMessage());
        }
        
        try {
            System.out.println("\n=== Testing Payment with Fallback ===");
            PaymentResult result = paymentService.processPaymentWithFallback(request);
            System.out.println("Payment successful via: " + result.getGateway());
            if (result.getFallbackReason() != null) {
                System.out.println("Fallback reason: " + result.getFallbackReason());
            }
        } catch (Exception e) {
            System.err.println("Payment failed: " + e.getMessage());
        }
        
        System.out.println("\n=== Testing Health Check ===");
        HealthStatus health = paymentService.checkPaymentGatewayHealth();
        System.out.println("Gateway health: " + health.getStatus() + " - " + health.getMessage());
    }
}
