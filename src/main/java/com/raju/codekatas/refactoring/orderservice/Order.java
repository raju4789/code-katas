package com.raju.codekatas.refactoring.orderservice;

import java.util.List;

class Item {

    private String itemName;
    private double price;
    private int quantity;

    public Item(String itemName, double price, int quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}

public class Order {
    private List<Item> items;
    private boolean isDiscountApplied;

    double calculateTotalPrice(Order order, double discountPercentage) {
        double total = 0;
        for (Item item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }

        if (order.isDiscountApplied()) {
            total = total * discountPercentage;
        }
        return total;
    }


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean isDiscountApplied() {
        return isDiscountApplied;
    }

    public void setDiscountApplied(boolean discountApplied) {
        isDiscountApplied = discountApplied;
    }

    public void validate() {
        if (items == null || items.isEmpty()) {
            System.out.println("Order has no items");
            throw new IllegalArgumentException("Order has no items");

        }
    }
}


