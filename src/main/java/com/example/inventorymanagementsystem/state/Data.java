package com.example.inventorymanagementsystem.state;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Data {
    private static Data instance;
    private ObservableList<Stock> stocks;
    private ObservableList<Color> colors;
    private ObservableList<Size> size;

    private Data(){
        stocks = FXCollections.observableArrayList(Connection.getInstance().getStocks());
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
}
