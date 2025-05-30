package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.state.ThemeObserver;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class TitleBar extends HBox implements com.example.inventorymanagementsystem.services.interfaces.ThemeObserver {
    Label label;
    Button minimize, restore, close;

    private final FontIcon minimizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MINIMIZE);
    private final FontIcon restoreIcon = new FontIcon(FontAwesomeSolid.WINDOW_RESTORE);
    private final FontIcon closeIcon = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);

    private final FontIcon lightTheme = new FontIcon(FontAwesomeSolid.SUN);
    private final FontIcon darkTheme = new FontIcon(FontAwesomeSolid.MOON);
    public TitleBar(Stage stage){
        this.setPadding(new Insets(5.0D, 5.0D, 5.0D, 10.0D));
        this.setSpacing(10.0D);
        label = new Label("Inventory Management System + Integrated POS features");
        label.setAlignment(Pos.CENTER_LEFT);
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        Menu about = new Menu("About");

        menuBar.getMenus().addAll(file, about);

        HBox commandButtons = new HBox();

        ToggleButton toggleTheme = new ToggleButton();
        toggleTheme.setGraphic(lightTheme);
        toggleTheme.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    toggleTheme.setGraphic(darkTheme);
                    ThemeObserver.init().applyLightThemeChange();
                }else{
                    toggleTheme.setGraphic(lightTheme);
                    ThemeObserver.init().applyDarkThemeChange();
                }
            }
        });

        minimize = new Button();
        minimize.getStyleClass().add("command-button");
        minimize.setGraphic(minimizeIcon);
        minimize.setOnAction(e -> {
            stage.setIconified(true);
        });

        restore = new Button();
        restore.getStyleClass().add("command-button");
        restore.setGraphic(restoreIcon);
        restore.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
        });

        close = new Button();
        close.getStyleClass().add("command-button");
        close.getStyleClass().add("close-button");
        close.setGraphic(closeIcon);
        close.setOnAction(actionEvent -> {
            stage.close();
        });

        commandButtons.getChildren().addAll(toggleTheme, minimize, restore, close);
        commandButtons.setMinWidth(120);
        commandButtons.setMaxWidth(120);
        commandButtons.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(label, menuBar, commandButtons);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        ThemeObserver.init().addObserver(this);
    }

    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: #EEE;");
        label.setStyle("-fx-text-fill: #000; ");

        minimizeIcon.setFill(Paint.valueOf("#000"));
        restoreIcon.setFill(Paint.valueOf("#000"));
        closeIcon.setFill(Paint.valueOf("#000"));
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #151515;");
        label.setStyle("-fx-text-fill: #EEE; ");

        minimizeIcon.setFill(Paint.valueOf("#FFF"));
        restoreIcon.setFill(Paint.valueOf("#FFF"));
        closeIcon.setFill(Paint.valueOf("#FFF"));
    }
}
