package com.example.inventorymanagementsystem.models;

public class Item {
    private int id;
    private String name;
    private double price;
    private double sellingprice;
    private int stockid;

    public Item(int id, String name, double price, double sellingprice, int stockid) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sellingprice = sellingprice;
        this.stockid = stockid;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public double getPrice() { return price; }

    public double getSellingprice() { return sellingprice; }

    public int getStockid() { return stockid; }


}

