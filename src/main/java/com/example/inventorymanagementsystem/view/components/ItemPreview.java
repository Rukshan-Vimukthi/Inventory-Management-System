package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class that extends VBox to show the information of the selected item.
 */
public class ItemPreview extends VBox {
    private Item item;

    /**
     *
     * @param item - Item object instance to show the data of.
     */
    public ItemPreview(Item item){
        this.item = item;

        this.setMinHeight(300.0D);
        this.setMaxHeight(300.0D);
        this.setPadding(new Insets(10.0D));

        if (item == null){
            Label label = new Label("No item data to preview");
            label.setFont(Font.font(18.0D));
            label.setTextAlignment(TextAlignment.CENTER);
            this.setAlignment(Pos.CENTER);
            this.getChildren().add(label);
        }else{
            // code to display the item information
        }
    }
}
