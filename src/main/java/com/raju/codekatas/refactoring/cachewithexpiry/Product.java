package com.raju.codekatas.refactoring.cachewithexpiry;

class Product {
    private final String id;
    private final String name;
    private final double price;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String toString() {
        return name + " ($" + price + ")";
    }
}
