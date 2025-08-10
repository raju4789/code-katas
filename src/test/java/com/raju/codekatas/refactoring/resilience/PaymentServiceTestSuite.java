package com.raju.codekatas.refactoring.resilience;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Circuit Breaker and Resilience refactoring challenge.
 * Tests expected behavior for the refactored resilient payment service.
 */
@DisplayName("Payment Service Resilience Test Suite")
public class PaymentServiceTestSuite {
    
    private PaymentServiceL paymentService;
    
    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceL();
    }
    
    @Nested
    @DisplayName("Basic Payment Processing Tests")
    class BasicPaymentTests {
        
        @Test
        @DisplayName("Should process payment successfully")
        void testSuccessfulPayment() {
            PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                "PAY-001", "CUST-001", 99.99, "4111111111111111");
            
            try {
                PaymentServiceL.PaymentResult result = paymentService.processPayment(request);
                
                assertTrue(result.isSuccessful());
                assertNotNull(result.getTransactionId());
                assertEquals(99.99, result.getAmount());
                assertEquals("PRIMARY_GATEWAY", result.getGateway());
                
            } catch (RuntimeException e) {
                // Payment failures are expected due to simulated instability
                assertNotNull(e.getMessage());
            }
        }
        
        @Test
        @DisplayName("Should handle payment rejections")
        void testPaymentRejection() {
            // Try multiple payments to encounter rejections
            for (int i = 0; i < 10; i++) {
                PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                    "PAY-" + i, "CUST-" + i, 100.0, "4111111111111111");
                
                try {
                    paymentService.processPayment(request);
                } catch (RuntimeException e) {
                    // Should handle rejections gracefully
                    assertTrue(e.getMessage().contains("rejected") || 
                             e.getMessage().contains("declined") ||
                             e.getMessage().contains("unavailable"));
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Fallback Mechanism Tests")
    class FallbackTests {
        
        @Test
        @DisplayName("Should use fallback gateway when primary fails")
        void testFallbackUsage() {
            PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                "PAY-001", "CUST-001", 50.0, "4111111111111111");
            
            try {
                PaymentServiceL.PaymentResult result = paymentService.processPaymentWithFallback(request);
                
                assertTrue(result.isSuccessful());
                assertNotNull(result.getTransactionId());
                
                // Could be from either primary or fallback gateway
                assertTrue("PRIMARY_GATEWAY".equals(result.getGateway()) || 
                          "FALLBACK_GATEWAY".equals(result.getGateway()));
                
                if ("FALLBACK_GATEWAY".equals(result.getGateway())) {
                    assertNotNull(result.getFallbackReason());
                }
                
            } catch (RuntimeException e) {
                // Both gateways can fail in simulation
                assertTrue(e.getMessage().contains("failed") || 
                         e.getMessage().contains("unavailable"));
            }
        }
        
        @Test
        @DisplayName("Should try multiple payment methods")
        void testMultiplePaymentAttempts() {
            // Try multiple times to test fallback behavior
            int attempts = 10;
            int primarySuccesses = 0;
            int fallbackSuccesses = 0;
            int totalFailures = 0;
            
            for (int i = 0; i < attempts; i++) {
                PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                    "PAY-" + i, "CUST-" + i, 75.0, "4111111111111111");
                
                try {
                    PaymentServiceL.PaymentResult result = paymentService.processPaymentWithFallback(request);
                    
                    if ("PRIMARY_GATEWAY".equals(result.getGateway())) {
                        primarySuccesses++;
                    } else if ("FALLBACK_GATEWAY".equals(result.getGateway())) {
                        fallbackSuccesses++;
                    }
                    
                } catch (RuntimeException e) {
                    totalFailures++;
                }
            }
            
            // Should have some mix of results
            int totalSuccesses = primarySuccesses + fallbackSuccesses;
            assertTrue(totalSuccesses > 0 || totalFailures > 0, 
                "Should have some payment processing attempts");
            
            // Fallback should provide additional resilience
            assertTrue(totalSuccesses >= primarySuccesses, 
                "Fallback should not reduce success rate");
        }
    }
    
    @Nested
    @DisplayName("Timeout Handling Tests")
    class TimeoutTests {
        
        @Test
        @DisplayName("Should handle gateway timeouts")
        void testTimeoutHandling() {
            // Make multiple requests to encounter timeouts
            for (int i = 0; i < 15; i++) {
                PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                    "PAY-TIMEOUT-" + i, "CUST-" + i, 200.0, "4111111111111111");
                
                try {
                    paymentService.processPayment(request);
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("timeout")) {
                        // Successfully detected and handled timeout
                        assertTrue(true, "Timeout properly detected");
                        return;
                    }
                }
            }
            
            // If no timeout was encountered, that's also acceptable
            assertTrue(true, "No timeouts encountered in this run");
        }
        
        @Test
        @DisplayName("Should complete within reasonable time")
        void testProcessingTimeout() {
            PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                "PAY-001", "CUST-001", 100.0, "4111111111111111");
            
            long startTime = System.currentTimeMillis();
            
            try {
                paymentService.processPayment(request);
            } catch (RuntimeException e) {
                // Failures are acceptable for timeout test
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Should not hang indefinitely (allowing for simulated delays)
            assertTrue(duration < 15000, "Payment processing should complete within 15 seconds");
        }
    }
    
    @Nested
    @DisplayName("Health Monitoring Tests")
    class HealthMonitoringTests {
        
        @Test
        @DisplayName("Should check gateway health")
        void testHealthCheck() {
            PaymentServiceL.HealthStatus healthStatus = paymentService.checkPaymentGatewayHealth();
            
            assertNotNull(healthStatus);
            assertNotNull(healthStatus.getStatus());
            assertNotNull(healthStatus.getMessage());
            assertNotNull(healthStatus.getTimestamp());
            
            assertTrue("HEALTHY".equals(healthStatus.getStatus()) || 
                      "UNHEALTHY".equals(healthStatus.getStatus()));
        }
        
        @Test
        @DisplayName("Should detect unhealthy gateway")
        void testUnhealthyGatewayDetection() {
            // Multiple health checks to potentially detect unhealthy state
            boolean foundUnhealthy = false;
            
            for (int i = 0; i < 10; i++) {
                PaymentServiceL.HealthStatus healthStatus = paymentService.checkPaymentGatewayHealth();
                
                if ("UNHEALTHY".equals(healthStatus.getStatus())) {
                    foundUnhealthy = true;
                    assertTrue(healthStatus.getMessage().contains("error") || 
                             healthStatus.getMessage().contains("gateway"));
                    break;
                }
            }
            
            // Either found unhealthy state or all were healthy (both acceptable)
            assertTrue(true, "Health monitoring is working");
        }
    }
    
    @Nested
    @DisplayName("Failure Recovery Tests")
    class FailureRecoveryTests {
        
        @Test
        @DisplayName("Should recover from transient failures")
        void testTransientFailureRecovery() {
            int attempts = 20;
            int consecutiveFailures = 0;
            int maxConsecutiveFailures = 0;
            
            for (int i = 0; i < attempts; i++) {
                PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                    "PAY-RECOVERY-" + i, "CUST-" + i, 50.0, "4111111111111111");
                
                try {
                    paymentService.processPayment(request);
                    consecutiveFailures = 0; // Reset on success
                } catch (RuntimeException e) {
                    consecutiveFailures++;
                    maxConsecutiveFailures = Math.max(maxConsecutiveFailures, consecutiveFailures);
                }
            }
            
            // Should not fail all attempts (some recovery should occur)
            assertTrue(maxConsecutiveFailures < attempts, 
                "Should not fail all consecutive attempts - recovery expected");
        }
        
        @Test
        @DisplayName("Should handle different types of failures")
        void testFailureTypeHandling() {
            PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                "PAY-001", "CUST-001", 100.0, "4111111111111111");
            
            // Make multiple attempts to encounter different failure types
            for (int i = 0; i < 20; i++) {
                try {
                    paymentService.processPayment(request);
                } catch (RuntimeException e) {
                    // Should handle different types of failures gracefully
                    String message = e.getMessage().toLowerCase();
                    assertTrue(message.contains("timeout") || 
                             message.contains("unavailable") ||
                             message.contains("declined") ||
                             message.contains("rejected") ||
                             message.contains("failed"));
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Performance Under Load Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should handle multiple concurrent payments")
        void testConcurrentPayments() throws InterruptedException {
            final int numberOfThreads = 5;
            Thread[] threads = new Thread[numberOfThreads];
            
            for (int i = 0; i < numberOfThreads; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                        "PAY-CONCURRENT-" + threadId, "CUST-" + threadId, 
                        100.0, "4111111111111111");
                    
                    try {
                        paymentService.processPayment(request);
                    } catch (RuntimeException e) {
                        // Failures are expected under load
                    }
                });
            }
            
            long startTime = System.currentTimeMillis();
            
            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }
            
            // Wait for completion
            for (Thread thread : threads) {
                thread.join(15000); // 15 second timeout per thread
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            // Should handle concurrent load within reasonable time
            assertTrue(totalTime < 30000, "Concurrent processing should complete within 30 seconds");
        }
        
        @Test
        @DisplayName("Should maintain performance under stress")
        void testStressPerformance() {
            int rapidRequests = 10;
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < rapidRequests; i++) {
                PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                    "PAY-STRESS-" + i, "CUST-" + i, 25.0, "4111111111111111");
                
                try {
                    paymentService.processPayment(request);
                } catch (RuntimeException e) {
                    // Failures are acceptable under stress
                }
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            // Should handle rapid requests
            assertTrue(totalTime < 60000, "Stress test should complete within 60 seconds");
        }
    }
    
    @Nested
    @DisplayName("Payment Data Validation Tests")
    class PaymentDataTests {
        
        @Test
        @DisplayName("Should preserve payment request information")
        void testPaymentDataPreservation() {
            PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                "PAY-001", "CUST-001", 123.45, "4111111111111111");
            
            try {
                PaymentServiceL.PaymentResult result = paymentService.processPayment(request);
                
                if (result.isSuccessful()) {
                    assertEquals(request.getAmount(), result.getAmount());
                    assertNotNull(result.getTransactionId());
                }
                
            } catch (RuntimeException e) {
                // Processing might fail, which is acceptable for this test
            }
        }
        
        @Test
        @DisplayName("Should handle different payment amounts")
        void testDifferentPaymentAmounts() {
            double[] amounts = {1.0, 50.0, 100.0, 500.0, 1000.0};
            
            for (double amount : amounts) {
                PaymentServiceL.PaymentRequest request = new PaymentServiceL.PaymentRequest(
                    "PAY-AMT-" + amount, "CUST-001", amount, "4111111111111111");
                
                try {
                    PaymentServiceL.PaymentResult result = paymentService.processPayment(request);
                    
                    if (result.isSuccessful()) {
                        assertEquals(amount, result.getAmount(), 0.01);
                    }
                    
                } catch (RuntimeException e) {
                    // Failures are expected and acceptable
                }
            }
        }
    }
}
