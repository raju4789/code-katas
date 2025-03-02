package com.raju.codekatas.orderingsystem.model;

import com.raju.codekatas.orderingsystem.payment.CashPayment;
import com.raju.codekatas.orderingsystem.payment.CreditCardPayment;
import com.raju.codekatas.orderingsystem.payment.PaymentMethod;
import com.raju.codekatas.orderingsystem.payment.WalletPayment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String name;
    private final int age;
    private final LocalDate birthday;
    private final List<PaymentMethod> paymentMethods;
    private Float walletAmount;

    public Customer(String name, Float walletAmount, int age, LocalDate birthday) {
        this.name = name;
        this.walletAmount = walletAmount;
        this.age = age;
        this.birthday = birthday;
        this.paymentMethods = new ArrayList<>();
        this.paymentMethods.add(new WalletPayment());
        this.paymentMethods.add(new CashPayment());
        this.paymentMethods.add(new CreditCardPayment());
    }

    public Float getWalletAmount() {
        return walletAmount;
    }

    public void deductFromWallet(Float amount) {
        this.walletAmount -= amount;
    }

    public int getAge() {
        return age;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public String getName() {
        return name;
    }
}