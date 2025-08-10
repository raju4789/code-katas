package com.raju.codekatas.refactoring.payment;

class PaypalHandler implements PaymentMethodHandler {

    private static final double PAYPAL_FEE = 0.03;

    @Override
    public void processPaymentForUser(String userId) {
        double totalFee = calculateTotalCharge(PAYPAL_FEE);
        System.out.println("Applying PayPal fee: " + totalFee);
        System.out.println("Charging PayPal account for user: " + userId);

    }
}
