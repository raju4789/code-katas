package com.raju.codekatas.refactoring.payment;

class CreditCardHandler implements PaymentMethodHandler {

    private static final double CREDIT_CARD_FEE = 0.02;

    @Override
    public void processPaymentForUser(String userId) {
        double totalFee = calculateTotalCharge(CREDIT_CARD_FEE);
        System.out.println("Applying credit card fee: " + totalFee);
        System.out.println("Charging credit card for user: " + userId);
    }
}
