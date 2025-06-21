package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.state.Constants;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EasyCheckoutItem extends VBox {
    private ItemDetail itemDetail;
    private Label itemName;
    private Label itemSize;
    private Label itemPrice;
    private Canvas itemColor;
    public EasyCheckoutItem(ItemDetail itemDetail){
        this.itemDetail = itemDetail;

        HBox details = new HBox();

        if (itemDetail != null) {
            itemName = new Label(itemDetail.getName());
            itemSize = new Label(itemDetail.getSize());
            itemPrice = new Label(Constants.CURRENCY_UNIT + "%.2f".formatted(itemDetail.getPrice()));
            itemColor = new Canvas();
        }
    }
}
