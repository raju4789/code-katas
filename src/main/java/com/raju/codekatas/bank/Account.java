package com.raju.codekatas.bank;

public interface Account {
    void deposit(int amount);

    void withdraw(int amount);

    String printStatement();
}
