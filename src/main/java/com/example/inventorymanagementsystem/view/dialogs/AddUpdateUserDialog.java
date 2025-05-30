package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.view.forms.AddUpdateUser;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class AddUpdateUserDialog extends Dialog<Boolean> {
    public AddUpdateUserDialog(User user){
        DialogPane dialogPane = new DialogPane();
        AddUpdateUser addUpdateUserForm = new AddUpdateUser(user, this);
        dialogPane.setContent(addUpdateUserForm);
        this.setDialogPane(dialogPane);
    }
}