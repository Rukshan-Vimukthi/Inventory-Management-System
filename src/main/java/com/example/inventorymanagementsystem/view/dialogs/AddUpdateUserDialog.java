package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.view.forms.AddUpdateUser;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.sql.SQLException;

public class AddUpdateUserDialog extends Dialog<Boolean> {
    public AddUpdateUserDialog(User user){
        DialogPane dialogPane = new DialogPane();
        dialogPane.setMinWidth(300.0D);
        dialogPane.setMaxWidth(300.0D);
        try {
            AddUpdateUser addUpdateUserForm = new AddUpdateUser(user, this);
            dialogPane.setContent(addUpdateUserForm);
        }catch(SQLException e){
            e.printStackTrace();
        }
        this.setDialogPane(dialogPane);
    }
}