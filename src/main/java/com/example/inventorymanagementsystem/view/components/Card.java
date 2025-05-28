package com.example.inventorymanagementsystem.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Card extends VBox {
    public String title, body, footer;

    private Label titleLabel;
    private Label bodyLabel;
    private Label footerLabel;

    private double accountBalance;

    public Card(String title, String body, String footer){
        super();
        this.title = title;
        this.body = body;
        this.footer = footer;

        titleLabel = new Label(title);
        bodyLabel = new Label(body);
        footerLabel = new Label(footer);

        this.getChildren().addAll(titleLabel, bodyLabel, footerLabel);
    }

    public void setTitle(String newTitle){
        titleLabel.setText(newTitle);
    }

    public void setBody(String newBody){
        bodyLabel.setText(newBody);
    }

    public void setFooter(String newFooter){
        footerLabel.setText(newFooter);
    }

    public void setAccountBalance(double newBalance){
        if(newBalance < 0){
            System.out.println("Incorrect balance");
        }
    }

}
