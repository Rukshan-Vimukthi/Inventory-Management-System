package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.ItemDetail;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddNewItem extends Dialog<Boolean> {
    public AddNewItem(ItemDetail itemDetail){
        DialogPane dialogPane = new DialogPane();

        VBox mainContainer = new VBox();

        HBox footer = new HBox();
        Button save = new Button("Save");
        Button saveAndAddAnother = new Button("Save & Add Another");
        Button close = new Button("Close");
        footer.getChildren().addAll(save, saveAndAddAnother, close);

        this.setDialogPane(dialogPane);
    }
}
