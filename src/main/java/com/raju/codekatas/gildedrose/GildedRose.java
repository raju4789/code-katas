package com.raju.codekatas.gildedrose;

/**
 * <a href="https://github.com/emilybache/GildedRose-Refactoring-Kata/blob/main/GildedRoseRequirements.md">Problem Description</a>
 * Refactor the code to make it more readable and maintainable using SOLID principles.
 */

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            item.updateItem(item);
        }

    }
}
