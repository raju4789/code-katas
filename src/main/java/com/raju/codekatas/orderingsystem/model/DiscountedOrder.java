package com.raju.codekatas.orderingsystem.model;

import java.util.List;

public record DiscountedOrder(Order originalOrder, Float discountedPrice, List<String> discountBreakdown) {

    // Method to format the discount breakdown for display
    public String formatDiscountBreakdown() {
        if (discountBreakdown.isEmpty()) {
            return "No discounts applied.";
        }

        StringBuilder breakdown = new StringBuilder("Discount Breakdown:\n");
        for (String discount : discountBreakdown) {
            breakdown.append("- ").append(discount).append("\n");
        }
        return breakdown.toString();
    }
}
