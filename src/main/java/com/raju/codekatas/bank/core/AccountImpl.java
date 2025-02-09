package com.raju.codekatas.bank.core;

import com.raju.codekatas.bank.Account;
import com.raju.codekatas.bank.exception.InsufficientBalanceException;

public class AccountImpl implements Account {

    private int balance;

    public AccountImpl(int balance) {
        this.balance = balance;
    }

    @Override
    public void deposit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be positive");
        }
        balance += amount;
    }

    @Override
    public void withdraw(int amount) throws InsufficientBalanceException {

        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        balance -= amount;

    }

    @Override
    public String printStatement() {
        return String.format("Your account balance is: %d", balance);
    }
}
