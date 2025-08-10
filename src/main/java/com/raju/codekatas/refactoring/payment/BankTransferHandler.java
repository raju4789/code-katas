package com.raju.codekatas.refactoring.payment;

class BankTransferHandler implements PaymentMethodHandler {

    private static final double BANK_TRANSFER_FEE = 0.01;

    @Override
    public void processPaymentForUser(String userId) {
        double totalFee = calculateTotalCharge(BANK_TRANSFER_FEE);
        System.out.println("Applying bank transfer fee: " + totalFee);
        System.out.println("Initiating bank transfer for user: " + userId);


    }
}
