package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.services.interfaces.AuthenticateStateListener;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Session;
import com.example.inventorymanagementsystem.state.ThemeObserver;
import com.example.inventorymanagementsystem.view.dialogs.About;
import com.example.inventorymanagementsystem.view.dialogs.SignIn;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class TitleBar extends HBox implements com.example.inventorymanagementsystem.services.interfaces.ThemeObserver, AuthenticateStateListener {
    Label label;
    Button authenticateButton, minimize, restore, close;

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
        Image image = new Image(String.valueOf(InventoryManagementApplication.class.getResource("images/sfc-logo.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50.0D);
        imageView.setFitHeight(20.0D);
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        Menu help = new Menu("Help");

        MenuItem about = new MenuItem("About Us");
        help.getItems().add(about);

        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                About aboutDialog = new About();
                aboutDialog.show();
            }
        });

        menuBar.getMenus().addAll(file, help);

        HBox commandButtons = new HBox();

        authenticateButton = new Button("Logout");
        authenticateButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFF; ");
        authenticateButton.setMinWidth(100.0D);
        HBox.setMargin(authenticateButton, new Insets(0.0D, 10.0D, 0.0D, 0.0D));
        if (Session.isLoggedIn()){
            authenticateButton.setText("Logout");
        }else{
            authenticateButton.setText("Login");
        }

        authenticateButton.setOnAction(actionEvent -> {
            if(Session.isLoggedIn()){
                Session.getInstance().destroy();
            }
        });

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

        commandButtons.getChildren().addAll(authenticateButton, toggleTheme, minimize, restore, close);
        commandButtons.setMinWidth(120);
        commandButtons.setMaxWidth(120);
        commandButtons.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(imageView, label, menuBar, commandButtons);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        ThemeObserver.init().addObserver(this);
        Session.addListener(this);
    }

    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: #EEE;");
        label.setStyle("-fx-text-fill: #000; ");

        minimizeIcon.setFill(Paint.valueOf("#000"));
        restoreIcon.setFill(Paint.valueOf("#000"));
        closeIcon.setFill(Paint.valueOf("#000"));
        this.getStylesheets().clear();
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #151515;");
        label.setStyle("-fx-text-fill: #EEE; ");

        minimizeIcon.setFill(Paint.valueOf("#FFF"));
        restoreIcon.setFill(Paint.valueOf("#FFF"));
        closeIcon.setFill(Paint.valueOf("#FFF"));

        this.getStylesheets().clear();
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
    }

    @Override
    public void onLoggedIn() {
        authenticateButton.setText("Log Out");
    }

    @Override
    public void onLoggedOut() {
        authenticateButton.setText("Log In");
    }
}
