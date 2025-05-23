package com.example.inventorymanagementsystem.view;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class Stock {
    private BorderPane mainLayout;

    public Stock() {
        mainLayout = new BorderPane();
        Text heading = new Text("Here we go!");
        
        mainLayout.setTop(heading);
    }

    public BorderPane getLayout() {
        return mainLayout;
    }
}
