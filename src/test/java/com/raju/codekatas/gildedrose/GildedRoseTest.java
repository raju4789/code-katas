package com.raju.codekatas.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    @Test
    public void testNormalItemBeforeSellDate() {
        Item[] items = new Item[]{new Item("Normal Item", 10, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals("Normal Item", items[0].getName());
        assertEquals(9, items[0].getSellIn());
        assertEquals(19, items[0].getQuality());
    }

    @Test
    public void testNormalItemOnSellDate() {
        Item[] items = new Item[]{new Item("Normal Item", 0, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, items[0].getSellIn());
        assertEquals(18, items[0].getQuality());
    }

    @Test
    public void testNormalItemAfterSellDate() {
        Item[] items = new Item[]{new Item("Normal Item", -1, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-2, items[0].getSellIn());
        assertEquals(18, items[0].getQuality());
    }

    @Test
    public void testNormalItemQualityNeverNegative() {
        Item[] items = new Item[]{new Item("Normal Item", 10, 0)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].getSellIn());
        assertEquals(0, items[0].getQuality());
    }

    @Test
    public void testAgedBrieBeforeSellDate() {
        Item[] items = new Item[]{new Item("Aged Brie", 10, 10)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].getSellIn());
        assertEquals(11, items[0].getQuality());
    }

    @Test
    public void testAgedBrieOnSellDate() {
        Item[] items = new Item[]{new Item("Aged Brie", 0, 10)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, items[0].getSellIn());
        assertEquals(12, items[0].getQuality());
    }

    @Test
    public void testAgedBrieAfterSellDate() {
        Item[] items = new Item[]{new Item("Aged Brie", -1, 10)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-2, items[0].getSellIn());
        assertEquals(12, items[0].getQuality());
    }

    @Test
    public void testAgedBrieQualityNeverExceeds50() {
        Item[] items = new Item[]{new Item("Aged Brie", 10, 50)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].getSellIn());
        assertEquals(50, items[0].getQuality());
    }

    @Test
    public void testSulfurasNeverChanges() {
        Item[] items = new Item[]{new Item("Sulfuras, Hand of Ragnaros", 10, 80)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(10, items[0].getSellIn());
        assertEquals(80, items[0].getQuality());
    }

    @Test
    public void testBackstagePassesLongBeforeSellDate() {
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(14, items[0].getSellIn());
        assertEquals(21, items[0].getQuality());
    }

    @Test
    public void testBackstagePassesCloseToSellDate() {
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].getSellIn());
        assertEquals(22, items[0].getQuality());
    }

    @Test
    public void testBackstagePassesVeryCloseToSellDate() {
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(4, items[0].getSellIn());
        assertEquals(23, items[0].getQuality());
    }

    @Test
    public void testBackstagePassesOnSellDate() {
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, items[0].getSellIn());
        assertEquals(0, items[0].getQuality());
    }

    @Test
    public void testBackstagePassesAfterSellDate() {
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", -1, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-2, items[0].getSellIn());
        assertEquals(0, items[0].getQuality());
    }

    @Test
    public void testBackstagePassesQualityNeverExceeds50() {
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(4, items[0].getSellIn());
        assertEquals(50, items[0].getQuality());
    }

    @Test
    public void testConjuredItemBeforeSellDate() {
        Item[] items = new Item[]{new Item("Conjured Mana Cake", 10, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].getSellIn());
        assertEquals(19, items[0].getQuality());
    }

    @Test
    public void testConjuredItemOnSellDate() {
        Item[] items = new Item[]{new Item("Conjured Mana Cake", 0, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, items[0].getSellIn());
        assertEquals(18, items[0].getQuality());
    }

    @Test
    public void testConjuredItemAfterSellDate() {
        Item[] items = new Item[]{new Item("Conjured Mana Cake", -1, 20)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-2, items[0].getSellIn());
        assertEquals(18, items[0].getQuality());
    }

    @Test
    public void testConjuredItemQualityNeverNegative() {
        Item[] items = new Item[]{new Item("Conjured Mana Cake", 10, 0)};
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].getSellIn());
        assertEquals(0, items[0].getQuality());
    }
}