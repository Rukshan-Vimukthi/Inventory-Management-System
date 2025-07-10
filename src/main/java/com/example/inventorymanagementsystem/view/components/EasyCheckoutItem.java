package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.state.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class EasyCheckoutItem extends VBox {
    private ItemDetail itemDetail;
    private Label itemName;
    private Label itemSize;
    private Label itemPrice;
    private Canvas itemColor;

    public EasyCheckoutItem(ItemDetail itemDetail, Consumer<ItemDetail> onSelect){

        this.itemDetail = itemDetail;
        HBox details = new HBox();
        details.setPadding(new Insets(0.0D, 0.0D, 0.0D, 10.0D));
        details.setSpacing(3);
        HBox valueContainer = new HBox(5);

        if (itemDetail != null) {
            itemName = new Label(itemDetail.getName());
            itemSize = new Label(itemDetail.getSize());
            itemPrice = new Label(Constants.CURRENCY_UNIT + "%.2f".formatted(itemDetail.getPrice()));

            itemName.getStyleClass().add("easy-checkout-item-name");
            itemSize.getStyleClass().add("easy-checkout-item-size");
            itemPrice.getStyleClass().add("easy-checkout-item-price");

            valueContainer.getChildren().addAll(itemSize, itemPrice);

            VBox infoBox = new VBox(2, itemName, valueContainer);
            Circle colorBox = new Circle(6);
            if (itemDetail.getItemColor() != null){
                colorBox.setFill(Color.web(itemDetail.getItemColor()));
            }
            colorBox.setStroke(Color.WHITE);
            colorBox.setStrokeWidth(1.5);
            HBox.setMargin(colorBox, new Insets(5.0D, 0.0D, 0.0D, 0.0D));
            details.getChildren().addAll(colorBox, infoBox);
        }
        this.getChildren().add(details);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("easy-checkout-card");
        this.setPrefHeight(9);
        this.setPrefWidth(140);
        this.setPadding(new Insets(0, 4, 0, 4));

        this.setOnMouseClicked(e -> {
            if (itemDetail != null && onSelect != null) {
                onSelect.accept(itemDetail);
            }
        });
    }
}
