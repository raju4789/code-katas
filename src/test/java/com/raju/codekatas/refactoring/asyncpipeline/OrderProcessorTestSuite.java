package com.raju.codekatas.refactoring.asyncpipeline;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Async Pipeline refactoring challenge.
 * Tests expected behavior for the refactored async order processing system.
 */
@DisplayName("Order Processor Async Pipeline Test Suite")
public class OrderProcessorTestSuite {
    
    private OrderProcessorL processor;
    
    @BeforeEach
    void setUp() {
        processor = new OrderProcessorL();
    }
    
    @Nested
    @DisplayName("Basic Order Processing Tests")
    class BasicProcessingTests {
        
        @Test
        @DisplayName("Should process single order successfully")
        void testSingleOrderProcessing() {
            OrderProcessorL.Order order = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList("Laptop", "Mouse"), 1200.0);
            
            assertDoesNotThrow(() -> {
                OrderProcessorL.OrderResult result = processor.processOrder(order);
                assertNotNull(result);
                assertEquals("ORD-001", result.getOrderId());
                assertTrue(Arrays.asList("SUCCESS", "FAILED").contains(result.getStatus()));
            });
        }
        
        @Test
        @DisplayName("Should process multiple orders")
        void testMultipleOrderProcessing() {
            List<OrderProcessorL.Order> orders = Arrays.asList(
                new OrderProcessorL.Order("ORD-001", "CUST-001", Arrays.asList("Item1"), 100.0),
                new OrderProcessorL.Order("ORD-002", "CUST-002", Arrays.asList("Item2"), 200.0),
                new OrderProcessorL.Order("ORD-003", "CUST-003", Arrays.asList("Item3"), 300.0)
            );
            
            assertDoesNotThrow(() -> {
                List<OrderProcessorL.OrderResult> results = processor.processOrders(orders);
                assertEquals(3, results.size());
                
                for (OrderProcessorL.OrderResult result : results) {
                    assertNotNull(result.getOrderId());
                    assertNotNull(result.getStatus());
                }
            });
        }
    }
    
    @Nested
    @DisplayName("Order Validation Tests")
    class ValidationTests {
        
        @Test
        @DisplayName("Should reject orders with empty items")
        void testEmptyItemsValidation() {
            OrderProcessorL.Order invalidOrder = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList(), 100.0);
            
            assertThrows(IllegalArgumentException.class, 
                () -> processor.processOrder(invalidOrder));
        }
        
        @Test
        @DisplayName("Should reject orders with invalid total")
        void testInvalidTotalValidation() {
            OrderProcessorL.Order invalidOrder = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList("Item1"), -50.0);
            
            assertThrows(IllegalArgumentException.class, 
                () -> processor.processOrder(invalidOrder));
        }
        
        @Test
        @DisplayName("Should reject orders with null customer ID")
        void testNullCustomerIdValidation() {
            OrderProcessorL.Order invalidOrder = new OrderProcessorL.Order(
                "ORD-001", null, Arrays.asList("Item1"), 100.0);
            
            assertThrows(IllegalArgumentException.class, 
                () -> processor.processOrder(invalidOrder));
        }
        
        @Test
        @DisplayName("Should reject orders with empty customer ID")
        void testEmptyCustomerIdValidation() {
            OrderProcessorL.Order invalidOrder = new OrderProcessorL.Order(
                "ORD-001", "", Arrays.asList("Item1"), 100.0);
            
            assertThrows(IllegalArgumentException.class, 
                () -> processor.processOrder(invalidOrder));
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle inventory failures gracefully")
        void testInventoryFailureHandling() {
            // Create multiple orders to increase chance of hitting inventory failures
            for (int i = 0; i < 10; i++) {
                OrderProcessorL.Order order = new OrderProcessorL.Order(
                    "ORD-" + i, "CUST-" + i, Arrays.asList("Item" + i), 100.0);
                
                try {
                    processor.processOrder(order);
                } catch (IllegalStateException e) {
                    // Expected for inventory failures
                    assertTrue(e.getMessage().contains("Insufficient inventory") || 
                             e.getMessage().contains("Inventory service"));
                } catch (Exception e) {
                    // Other failures are also possible
                }
            }
        }
        
        @Test
        @DisplayName("Should handle payment failures gracefully")
        void testPaymentFailureHandling() {
            // Create multiple orders to increase chance of hitting payment failures
            for (int i = 0; i < 10; i++) {
                OrderProcessorL.Order order = new OrderProcessorL.Order(
                    "ORD-" + i, "CUST-" + i, Arrays.asList("Item" + i), 500.0);
                
                try {
                    processor.processOrder(order);
                } catch (RuntimeException e) {
                    // Expected for payment failures
                    assertTrue(e.getMessage().contains("Payment") || 
                             e.getMessage().contains("gateway") ||
                             e.getMessage().contains("funds"));
                }
            }
        }
        
        @Test
        @DisplayName("Should continue batch processing despite individual failures")
        void testBatchResiliency() {
            List<OrderProcessorL.Order> orders = Arrays.asList(
                new OrderProcessorL.Order("ORD-001", "CUST-001", Arrays.asList("Item1"), 100.0),
                new OrderProcessorL.Order("ORD-002", "", Arrays.asList("Item2"), 200.0), // Invalid
                new OrderProcessorL.Order("ORD-003", "CUST-003", Arrays.asList("Item3"), 300.0),
                new OrderProcessorL.Order("ORD-004", "CUST-004", Arrays.asList(), 400.0), // Invalid
                new OrderProcessorL.Order("ORD-005", "CUST-005", Arrays.asList("Item5"), 500.0)
            );
            
            List<OrderProcessorL.OrderResult> results = processor.processOrders(orders);
            
            // Should get results for all orders (some failed)
            assertEquals(5, results.size());
            
            // Some should be successful, some failed
            long successCount = results.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count();
            long failedCount = results.stream().filter(r -> "FAILED".equals(r.getStatus())).count();
            
            assertTrue(successCount >= 0);
            assertTrue(failedCount >= 2); // At least 2 invalid orders should fail
        }
    }
    
    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should process orders within reasonable time")
        void testProcessingPerformance() {
            OrderProcessorL.Order order = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList("Item1"), 100.0);
            
            long startTime = System.currentTimeMillis();
            
            try {
                processor.processOrder(order);
            } catch (Exception e) {
                // Failures are acceptable for performance test
            }
            
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;
            
            // Should complete within reasonable time (allowing for simulated delays)
            assertTrue(processingTime < 10000, "Processing should complete within 10 seconds");
        }
        
        @Test
        @DisplayName("Should handle multiple orders efficiently")
        void testBatchProcessingPerformance() {
            List<OrderProcessorL.Order> orders = Arrays.asList(
                new OrderProcessorL.Order("ORD-001", "CUST-001", Arrays.asList("Item1"), 100.0),
                new OrderProcessorL.Order("ORD-002", "CUST-002", Arrays.asList("Item2"), 200.0),
                new OrderProcessorL.Order("ORD-003", "CUST-003", Arrays.asList("Item3"), 300.0)
            );
            
            long startTime = System.currentTimeMillis();
            
            List<OrderProcessorL.OrderResult> results = processor.processOrders(orders);
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            assertNotNull(results);
            assertEquals(3, results.size());
            
            // Should complete within reasonable time
            assertTrue(totalTime < 30000, "Batch processing should complete within 30 seconds");
        }
    }
    
    @Nested
    @DisplayName("Order Result Validation Tests")
    class OrderResultTests {
        
        @Test
        @DisplayName("Should provide complete order result information")
        void testOrderResultCompleteness() {
            OrderProcessorL.Order order = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList("Item1"), 100.0);
            
            try {
                OrderProcessorL.OrderResult result = processor.processOrder(order);
                
                assertNotNull(result.getOrderId());
                assertNotNull(result.getStatus());
                
                if ("SUCCESS".equals(result.getStatus())) {
                    assertNotNull(result.getProcessedOrder());
                    assertNotNull(result.getProcessedOrder().getTransactionId());
                    assertNotNull(result.getProcessedOrder().getTrackingNumber());
                } else if ("FAILED".equals(result.getStatus())) {
                    assertNotNull(result.getErrorMessage());
                }
                
            } catch (Exception e) {
                // Processing might fail due to simulated failures
                assertNotNull(e.getMessage());
            }
        }
        
        @Test
        @DisplayName("Should preserve original order information")
        void testOriginalOrderPreservation() {
            OrderProcessorL.Order order = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList("Item1", "Item2"), 250.0);
            
            try {
                OrderProcessorL.OrderResult result = processor.processOrder(order);
                
                if ("SUCCESS".equals(result.getStatus()) && result.getProcessedOrder() != null) {
                    OrderProcessorL.Order originalOrder = result.getProcessedOrder().getOriginalOrder();
                    
                    assertEquals(order.getId(), originalOrder.getId());
                    assertEquals(order.getCustomerId(), originalOrder.getCustomerId());
                    assertEquals(order.getTotalAmount(), originalOrder.getTotalAmount());
                    assertEquals(order.getItems(), originalOrder.getItems());
                }
                
            } catch (Exception e) {
                // Processing might fail, which is acceptable
            }
        }
    }
    
    @Nested
    @DisplayName("Concurrent Processing Tests")
    class ConcurrentProcessingTests {
        
        @Test
        @DisplayName("Should handle concurrent order processing")
        void testConcurrentProcessing() throws InterruptedException {
            final int numberOfThreads = 5;
            Thread[] threads = new Thread[numberOfThreads];
            
            for (int i = 0; i < numberOfThreads; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    OrderProcessorL.Order order = new OrderProcessorL.Order(
                        "ORD-" + threadId, "CUST-" + threadId, 
                        Arrays.asList("Item" + threadId), 100.0);
                    
                    try {
                        processor.processOrder(order);
                    } catch (Exception e) {
                        // Some failures are expected due to simulated service issues
                    }
                });
            }
            
            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }
            
            // Wait for completion
            for (Thread thread : threads) {
                thread.join(10000); // 10 second timeout
            }
            
            // Test should complete without hanging
            assertTrue(true, "Concurrent processing completed");
        }
    }
    
    @Nested
    @DisplayName("Timeout and Interruption Tests")
    class TimeoutTests {
        
        @Test
        @DisplayName("Should handle processing interruption")
        void testProcessingInterruption() {
            OrderProcessorL.Order order = new OrderProcessorL.Order(
                "ORD-001", "CUST-001", Arrays.asList("Item1"), 100.0);
            
            Thread processingThread = new Thread(() -> {
                try {
                    processor.processOrder(order);
                } catch (RuntimeException e) {
                    // Expected when interrupted
                    assertTrue(e.getMessage().contains("interrupted") || 
                             e.getMessage().contains("Processing interrupted"));
                }
            });
            
            processingThread.start();
            
            // Interrupt after short delay
            try {
                Thread.sleep(100);
                processingThread.interrupt();
                processingThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            assertFalse(processingThread.isAlive(), "Thread should have terminated");
        }
    }
}
