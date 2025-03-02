package com.raju.codekatas.orderingsystem.payment;

import com.raju.codekatas.orderingsystem.model.Customer;

public class WalletPayment implements PaymentMethod {
    @Override
    public boolean pay(Float amount, Customer customer) {
        if (customer.getWalletAmount() >= amount) {
            customer.deductFromWallet(amount);
            return true;
        }
        return false;
    }
}
