package com.example.inventorymanagementsystem.state;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Data {
    private static Data instance;
    private ObservableList<Stock> stocks;
    private ObservableList<Color> colors;
    private ObservableList<Size> size;
    private ObservableList<ItemDetail> itemDetails;

    /**
     * initializes the Data object instance when the class get loaded
     */
    static {
        Data.getInstance();
        System.out.println("Data instance created!");
    }

    private Data(){
        Connection connection = Connection.getInstance();
        stocks = FXCollections.observableArrayList(connection.getStocks());
        colors = FXCollections.observableArrayList(connection.getColors());
        size = FXCollections.observableArrayList(connection.getSizes());
        itemDetails = FXCollections.observableArrayList(connection.getItemDetails());
    }

    public static Data getInstance(){
        if (instance == null){
            instance = new Data();
        }
        return instance;
    }

    public ObservableList<Stock> getStocks(){
        return stocks;
    }

    public ObservableList<Color> getColors() {
        return colors;
    }

    public ObservableList<Size> getSize() {
        return size;
    }

    public ObservableList<ItemDetail> getItemDetails() {
        return itemDetails;
    }
}
