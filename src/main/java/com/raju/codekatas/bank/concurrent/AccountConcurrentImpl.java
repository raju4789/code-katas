package com.raju.codekatas.bank.concurrent;

import com.raju.codekatas.bank.Account;
import com.raju.codekatas.bank.exception.InsufficientBalanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class AccountConcurrentImpl implements Account {

    private static final Logger logger = LoggerFactory.getLogger(AccountConcurrentImpl.class);

    private final Lock lock = new ReentrantLock();

    private int balance;

    public AccountConcurrentImpl(int balance) {
        this.balance = balance;
    }

    @Override
    public void deposit(int amount) {
        lock.lock();
        try {
            if (amount < 0) {
                logger.warn("Invalid deposit amount: {}", amount);
                throw new IllegalArgumentException("Amount should be positive");
            }
            balance += amount;
            logger.info("Deposited {}: New Balance: {}", amount, balance);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void withdraw(int amount) {
        lock.lock();
        try {
            if (amount > balance) {
                logger.error("Withdrawal failed: Insufficient funds. Requested: {}, Available: {}", amount, balance);
                throw new InsufficientBalanceException("Insufficient balance");
            }
            balance -= amount;
            logger.info("Withdrawn {}: New Balance: {}", amount, balance);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public String printStatement() {
        lock.lock();
        try {
            logger.info("Account Balance Requested: {}", balance);
            return String.format("Your account balance is: %d", balance);
        } finally {
            lock.unlock();
        }
    }
}
