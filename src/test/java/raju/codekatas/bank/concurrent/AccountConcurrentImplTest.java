package raju.codekatas.bank.concurrent;

import com.raju.codekatas.bank.concurrent.AccountConcurrentImpl;
import com.raju.codekatas.bank.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountConcurrentImplTest {

    private AccountConcurrentImpl account;

    @BeforeEach
    void setUp() {
        account = new AccountConcurrentImpl(100); // Initial balance = 100
    }

    @Test
    @DisplayName("should deposit amount successfully")
    void testDeposit() {
        account.deposit(50);
        assertEquals("Your account balance is: 150", account.printStatement()); // Balance should be 150
    }

    @Test
    @DisplayName("should withdraw amount successfully")
    void testWithdraw() {
        account.withdraw(50);
        assertEquals("Your account balance is: 50", account.printStatement()); // Balance should be 50
    }

    @Test
    @DisplayName("should throw exception when withdrawing more than balance")
    void testWithdrawInsufficientFunds() {
        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(200));
    }

    @Test
    @DisplayName("should handle concurrent deposits correctly")
    void testConcurrentDeposits() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int depositAmount = 50;
        int numDeposits = 10; // 10 deposits of 50 each => +500

        CountDownLatch latch = new CountDownLatch(numDeposits);
        for (int i = 0; i < numDeposits; i++) {
            executor.submit(() -> {
                try {
                    account.deposit(depositAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        assertEquals("Your account balance is: 600", account.printStatement()); // 100 + 500 = 600
    }

    @Test
    @DisplayName("should handle concurrent withdrawals correctly")
    void testConcurrentWithdrawals() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int withdrawAmount = 20;
        int numWithdrawals = 5; // 5 withdrawals of 20 each => -100

        CountDownLatch latch = new CountDownLatch(numWithdrawals);
        for (int i = 0; i < numWithdrawals; i++) {
            executor.submit(() -> {
                try {
                    account.withdraw(withdrawAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        assertEquals("Your account balance is: 0", account.printStatement()); // 100 - 100 = 0
    }

    @Test
    @DisplayName("should handle concurrent deposits and withdrawals correctly")
    void testConcurrentDepositsAndWithdrawals() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int depositAmount = 50;
        int withdrawAmount = 30;
        int numOperations = 5;

        CountDownLatch latch = new CountDownLatch(numOperations * 2);
        for (int i = 0; i < numOperations; i++) {
            executor.submit(() -> {
                try {
                    account.deposit(depositAmount);
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    account.withdraw(withdrawAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        assertEquals("Your account balance is: 200", account.printStatement()); // 100 + 250 - 150 = 200
    }

    @Test
    @DisplayName("should handle concurrent deposits and withdrawals correctly with withdraw more than balance")
    void testConcurrentDepositsAndWithdrawalsWithInsufficientFunds() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int depositAmount = 50;
        int withdrawAmount = 200;
        int numOperations = 5;

        CountDownLatch latch = new CountDownLatch(numOperations * 2);
        for (int i = 0; i < numOperations; i++) {
            executor.submit(() -> {
                try {
                    account.deposit(depositAmount);
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    assertThrows(InsufficientBalanceException.class, () -> account.withdraw(withdrawAmount));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        assertEquals("Your account balance is: 150", account.printStatement()); // 100 + 250 - 100 = 250
    }
}