package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.view.forms.AddUpdateUser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

public class AddUpdateUserDialog extends Stage {
    User user;
    Scene scene;
    public AddUpdateUserDialog(User user){
        this.user = user;
        this.setMinWidth(300.0D);
        this.setMaxWidth(300.0D);
        this.initStyle(StageStyle.TRANSPARENT);
        try {
            AddUpdateUser addUpdateUserForm = new AddUpdateUser(user, this);
            scene = new Scene(addUpdateUserForm);
            scene.setFill(Paint.valueOf("#0000"));
            this.scene.getStylesheets().addAll(Constants.DARK_THEME_CSS, Constants.BASE_STYLES);
            this.setScene(this.scene);
        }catch(SQLException e){
            e.printStackTrace();
        }
        this.setAlwaysOnTop(true);
        this.show();
    }
}