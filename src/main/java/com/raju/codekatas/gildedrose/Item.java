package com.raju.codekatas.gildedrose;

public class Item {

    private String name;

    private int sellIn;

    private int quality;

    public Item(String name, int sellIn, int quality) {
        this.name = name;
        this.sellIn = sellIn;
        this.quality = quality;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getSellIn() {
        return sellIn;
    }

    public void setSellIn(int sellIn) {
        this.sellIn = sellIn;
    }

    public void updateItem(Item item) {
        boolean agedBrie = item.getName().equals("Aged Brie");
        boolean isBackStagePasses = item.getName().equals("Backstage passes to a TAFKAL80ETC concert");
        boolean isSulfuras = item.getName().equals("Sulfuras, Hand of Ragnaros");
        if (!agedBrie && !isBackStagePasses) {
            if (item.getQuality() > 0) {
                if (!isSulfuras) {
                    item.setQuality(item.getQuality() - 1);
                }
            }
        } else {
            if (item.getQuality() < 50) {
                item.setQuality(item.getQuality() + 1);

                if (isBackStagePasses) {
                    if (item.getSellIn() < 11) {
                        if (item.getQuality() < 50) {
                            item.setQuality(item.getQuality() + 1);
                            ;
                        }
                    }

                    if (item.getSellIn() < 6) {
                        if (item.getQuality() < 50) {
                            item.setQuality(item.getQuality() + 1);
                        }
                    }
                }
            }
        }

        if (!isSulfuras) {
            item.setSellIn(item.getSellIn() - 1);
        }

        if (item.getSellIn() < 0) {
            if (!agedBrie) {
                if (!isBackStagePasses) {
                    if (item.getQuality() > 0) {
                        if (!isSulfuras) {
                            item.setQuality(item.getQuality() - 1);
                        }
                    }
                } else {
                    item.setQuality(0);
                }
            } else {
                if (item.getQuality() < 50) {
                    item.setQuality(item.getQuality() + 1);
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.getName() + ", " + this.getSellIn() + ", " + this.getQuality();
    }
}
