package com.example.inventorymanagementsystem.view;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

// this is test

public class Checkout {
    private HBox checkoutContainer;

    public Checkout () {
        checkoutContainer = new HBox();
        checkoutContainer.setSpacing(10);
        checkoutContainer.setAlignment(Pos.TOP_CENTER);

        Text heading = new Text("Checkout Panel");
        heading.setY(100);
        heading.setFont(Font.font("Verdana", 30));

        checkoutContainer.getChildren().addAll(heading);
    }

    public HBox getLayout() {
        return checkoutContainer;
    }

}
