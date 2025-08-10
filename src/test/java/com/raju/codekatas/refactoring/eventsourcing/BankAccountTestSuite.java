package com.raju.codekatas.refactoring.eventsourcing;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Event Sourcing refactoring challenge.
 * Tests expected behavior for the refactored bank account system.
 */
@DisplayName("Bank Account Event Sourcing Test Suite")
public class BankAccountTestSuite {
    
    private BankAccountL account;
    
    @BeforeEach
    void setUp() {
        account = new BankAccountL("ACC-001", "John Doe");
    }
    
    @Nested
    @DisplayName("Account Creation Tests")
    class AccountCreationTests {
        
        @Test
        @DisplayName("Should create account with initial state")
        void testAccountCreation() {
            assertEquals("ACC-001", account.getAccountId());
            assertEquals("John Doe", account.getCustomerName());
            assertEquals(0.0, account.getBalance());
            assertNotNull(account.getCreatedAt());
        }
        
        @Test
        @DisplayName("Should record account creation in transaction history")
        void testCreationInHistory() {
            List<String> history = account.getTransactionHistory();
            assertFalse(history.isEmpty());
            assertTrue(history.get(0).contains("Account created"));
            assertTrue(history.get(0).contains("John Doe"));
        }
    }
    
    @Nested
    @DisplayName("Deposit Tests")
    class DepositTests {
        
        @Test
        @DisplayName("Should process deposit successfully")
        void testSuccessfulDeposit() {
            account.deposit(100.0, "Initial deposit");
            assertEquals(100.0, account.getBalance());
        }
        
        @Test
        @DisplayName("Should process multiple deposits")
        void testMultipleDeposits() {
            account.deposit(100.0, "First deposit");
            account.deposit(50.0, "Second deposit");
            assertEquals(150.0, account.getBalance());
        }
        
        @Test
        @DisplayName("Should reject negative deposit")
        void testNegativeDeposit() {
            assertThrows(IllegalArgumentException.class, 
                () -> account.deposit(-50.0, "Invalid deposit"));
        }
        
        @Test
        @DisplayName("Should record deposit in transaction history")
        void testDepositInHistory() {
            account.deposit(100.0, "Test deposit");
            
            List<String> history = account.getTransactionHistory();
            boolean hasDeposit = history.stream()
                .anyMatch(tx -> tx.contains("Deposited $100.00") && tx.contains("Test deposit"));
            assertTrue(hasDeposit);
        }
    }
    
    @Nested
    @DisplayName("Withdrawal Tests")
    class WithdrawalTests {
        
        @BeforeEach
        void setUpWithBalance() {
            account.deposit(200.0, "Setup deposit");
        }
        
        @Test
        @DisplayName("Should process withdrawal successfully")
        void testSuccessfulWithdrawal() {
            account.withdraw(50.0, "ATM withdrawal");
            assertEquals(150.0, account.getBalance());
        }
        
        @Test
        @DisplayName("Should reject withdrawal with insufficient funds")
        void testInsufficientFunds() {
            assertThrows(IllegalStateException.class, 
                () -> account.withdraw(300.0, "Overdraft attempt"));
        }
        
        @Test
        @DisplayName("Should record failed withdrawal in history")
        void testFailedWithdrawalInHistory() {
            try {
                account.withdraw(300.0, "Overdraft attempt");
            } catch (IllegalStateException e) {
                // Expected
            }
            
            List<String> history = account.getTransactionHistory();
            boolean hasFailedWithdrawal = history.stream()
                .anyMatch(tx -> tx.contains("FAILED WITHDRAWAL"));
            assertTrue(hasFailedWithdrawal);
        }
        
        @Test
        @DisplayName("Should reject negative withdrawal")
        void testNegativeWithdrawal() {
            assertThrows(IllegalArgumentException.class, 
                () -> account.withdraw(-25.0, "Invalid withdrawal"));
        }
    }
    
    @Nested
    @DisplayName("Transfer Tests")
    class TransferTests {
        
        private BankAccountL toAccount;
        
        @BeforeEach
        void setUpTransfer() {
            account.deposit(300.0, "Setup deposit");
            toAccount = new BankAccountL("ACC-002", "Jane Smith");
        }
        
        @Test
        @DisplayName("Should transfer between accounts successfully")
        void testSuccessfulTransfer() {
            account.transfer(toAccount, 100.0, "Payment to Jane");
            
            assertEquals(200.0, account.getBalance());
            assertEquals(100.0, toAccount.getBalance());
        }
        
        @Test
        @DisplayName("Should reject transfer with insufficient funds")
        void testInsufficientFundsTransfer() {
            assertThrows(IllegalStateException.class, 
                () -> account.transfer(toAccount, 400.0, "Overdraft transfer"));
        }
        
        @Test
        @DisplayName("Should record transfer in both accounts")
        void testTransferInBothHistories() {
            account.transfer(toAccount, 100.0, "Test transfer");
            
            List<String> fromHistory = account.getTransactionHistory();
            List<String> toHistory = toAccount.getTransactionHistory();
            
            boolean hasTransferOut = fromHistory.stream()
                .anyMatch(tx -> tx.contains("Transferred $100.00"));
            boolean hasTransferIn = toHistory.stream()
                .anyMatch(tx -> tx.contains("Received $100.00"));
            
            assertTrue(hasTransferOut);
            assertTrue(hasTransferIn);
        }
        
        @Test
        @DisplayName("Should reject negative transfer")
        void testNegativeTransfer() {
            assertThrows(IllegalArgumentException.class, 
                () -> account.transfer(toAccount, -50.0, "Invalid transfer"));
        }
    }
    
    @Nested
    @DisplayName("Historical Balance Tests")
    class HistoricalBalanceTests {
        
        @Test
        @DisplayName("Should calculate historical balance correctly")
        void testHistoricalBalance() {
            LocalDateTime beforeDeposit = LocalDateTime.now();
            
            try {
                Thread.sleep(10); // Small delay to ensure timestamp difference
                account.deposit(100.0, "First deposit");
                Thread.sleep(10);
                account.withdraw(30.0, "First withdrawal");
                
                // Balance at the beginning should be 0
                double balanceAtStart = account.getBalanceAtTime(beforeDeposit);
                assertEquals(0.0, balanceAtStart, 0.01);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        @Test
        @DisplayName("Should handle future date queries")
        void testFutureBalanceQuery() {
            account.deposit(100.0, "Test deposit");
            
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
            double futureBalance = account.getBalanceAtTime(futureDate);
            
            assertEquals(account.getBalance(), futureBalance);
        }
    }
    
    @Nested
    @DisplayName("Transaction History Tests")
    class TransactionHistoryTests {
        
        @Test
        @DisplayName("Should maintain complete transaction history")
        void testCompleteHistory() {
            account.deposit(100.0, "First deposit");
            account.withdraw(30.0, "First withdrawal");
            account.deposit(50.0, "Second deposit");
            
            List<String> history = account.getTransactionHistory();
            
            // Should have creation + 3 transactions = 4 entries
            assertEquals(4, history.size());
        }
        
        @Test
        @DisplayName("Should include timestamps in transaction history")
        void testHistoryTimestamps() {
            account.deposit(100.0, "Test deposit");
            
            List<String> history = account.getTransactionHistory();
            
            // All transactions should have timestamps
            for (String transaction : history) {
                assertTrue(transaction.contains(":"), 
                    "Transaction should contain timestamp: " + transaction);
            }
        }
        
        @Test
        @DisplayName("Should include balance changes in history")
        void testBalanceChangesInHistory() {
            account.deposit(100.0, "Test deposit");
            
            List<String> history = account.getTransactionHistory();
            boolean hasBalanceInfo = history.stream()
                .anyMatch(tx -> tx.contains("Balance:") && tx.contains("->"));
            
            assertTrue(hasBalanceInfo);
        }
    }
    
    @Nested
    @DisplayName("Account Information Tests")
    class AccountInformationTests {
        
        @Test
        @DisplayName("Should provide complete account information")
        void testAccountInfo() {
            account.deposit(100.0, "Test deposit");
            String info = account.getAccountInfo();
            
            assertTrue(info.contains("ACC-001"));
            assertTrue(info.contains("John Doe"));
            assertTrue(info.contains("$100.00"));
        }
        
        @Test
        @DisplayName("Should update last modified timestamp")
        void testLastModifiedUpdate() {
            LocalDateTime before = account.getLastModified();
            
            try {
                Thread.sleep(10);
                account.deposit(100.0, "Test deposit");
                
                assertTrue(account.getLastModified().isAfter(before));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Nested
    @DisplayName("Immutability and Thread Safety Tests")
    class ThreadSafetyTests {
        
        @Test
        @DisplayName("Should return defensive copy of transaction history")
        void testDefensiveCopyOfHistory() {
            account.deposit(100.0, "Test deposit");
            
            List<String> history1 = account.getTransactionHistory();
            List<String> history2 = account.getTransactionHistory();
            
            // Should be different instances
            assertNotSame(history1, history2);
            
            // But same content
            assertEquals(history1, history2);
        }
        
        @Test
        @DisplayName("Should handle concurrent deposits safely")
        void testConcurrentDeposits() throws InterruptedException {
            final int numberOfThreads = 10;
            final double depositAmount = 10.0;
            
            Thread[] threads = new Thread[numberOfThreads];
            
            for (int i = 0; i < numberOfThreads; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    try {
                        account.deposit(depositAmount, "Deposit from thread " + threadId);
                    } catch (Exception e) {
                        // Some operations might fail due to concurrency issues
                        // Your refactored solution should handle this better
                    }
                });
            }
            
            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }
            
            // Wait for completion
            for (Thread thread : threads) {
                thread.join();
            }
            
            // Balance should be positive and transaction history should exist
            assertTrue(account.getBalance() >= 0);
            assertFalse(account.getTransactionHistory().isEmpty());
        }
    }
}
