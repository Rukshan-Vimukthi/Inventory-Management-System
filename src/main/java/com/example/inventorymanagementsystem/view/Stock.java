package com.example.inventorymanagementsystem.view;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;


public class Stock extends VBox {
    public Stock(){
        Text heading = new Text("Here we go!");
        this.getChildren().addAll(heading);
    }
}
