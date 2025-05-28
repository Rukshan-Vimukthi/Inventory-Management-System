package com.example.inventorymanagementsystem.models;

public class SoldProducts {
    private final int itemId;
    private final int totalSold;

    public SoldProducts(int itemId, int totalSold) {
        this.itemId = itemId;
        this.totalSold = totalSold;
    }

    public int getItemId() {
        return itemId;
    }

    public int getTotalSold() {
        return totalSold;
    }
}
