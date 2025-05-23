package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.Size;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class AddNewSize extends Dialog<Boolean> {
    public AddNewSize(Size size){
        DialogPane dialogPane = new DialogPane();
        this.setDialogPane(dialogPane);
    }
}
