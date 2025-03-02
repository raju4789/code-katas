package com.raju.codekatas.gildedrose.enums;

public enum ItemName {
    AGED_BRIE("Aged Brie"),
    BACKSTAGE_PASSES("Backstage passes to a TAFKAL80ETC concert"),
    SULFURAS("Sulfuras, Hand of Ragnaros");

    private final String name;

    ItemName(String name) {
        this.name = name;
    }

    public static ItemName fromName(String name) {
        for (ItemName itemName : ItemName.values()) {
            if (itemName.getName().equals(name)) {
                return itemName;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
