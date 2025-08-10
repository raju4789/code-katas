package com.raju.codekatas.refactoring.order;

public class OnlineOrderHandler implements OrderHandler {

    private static final double VIP_DISCOUNT = 0.1;

    @Override
    public void process(String order) {
        System.out.println("Processing order: " + order);
        if (isVip(order)) {
            double finalPrice = calculateFinalPrice(BASE_PRICE, VIP_DISCOUNT);
            System.out.println("VIP discount applied. Final price: " + finalPrice);
        } else {
            System.out.println("No discount. Price: " + BASE_PRICE);
        }
        System.out.println("Sending email confirmation for order: " + order);
    }
}
