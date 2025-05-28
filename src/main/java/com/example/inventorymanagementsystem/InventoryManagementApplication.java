package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.state.Data;

import com.example.inventorymanagementsystem.state.ThemeObserver;
import com.example.inventorymanagementsystem.view.Analytics;
import com.example.inventorymanagementsystem.view.Checkout;
import com.example.inventorymanagementsystem.view.Inventory;
import com.example.inventorymanagementsystem.view.Users;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import com.example.inventorymanagementsystem.view.dialogs.SignIn;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.Ikonli;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class InventoryManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(InventoryManagementApplication.class.getResource("ApplicationUI.fxml"));
        VBox rootContainer = new VBox();
        HBox titleBar = new HBox();
        titleBar.setPadding(new Insets(5.0D));
        titleBar.setSpacing(10.0D);
        Label label = new Label("Inventory Management System + Integrated POS features");
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        Menu about = new Menu("About");

        menuBar.getMenus().addAll(file, about);

        HBox commandButtons = new HBox();
        FontIcon minimizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MINIMIZE);
        FontIcon restoreIcon = new FontIcon(FontAwesomeSolid.WINDOW_RESTORE);
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);

        FontIcon lightTheme = new FontIcon(FontAwesomeSolid.SUN);
        FontIcon darkTheme = new FontIcon(FontAwesomeSolid.MOON);

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

        Button minimize = new Button();
        minimize.setGraphic(minimizeIcon);
        minimize.setOnAction(e -> {
            stage.setIconified(true);
        });
        Button restore = new Button();
        restore.setGraphic(restoreIcon);
        restore.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
        });
        Button close = new Button();
        close.setGraphic(closeIcon);
        close.setOnAction(actionEvent -> {
            stage.close();
        });

        commandButtons.getChildren().addAll(toggleTheme, minimize, restore, close);
        commandButtons.setMinWidth(120);
        commandButtons.setMaxWidth(120);
        commandButtons.setAlignment(Pos.CENTER_RIGHT);

        titleBar.getChildren().addAll(label, menuBar, commandButtons);
        HBox.setHgrow(menuBar, Priority.ALWAYS);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(Side.LEFT);
        tabPane.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setRotateGraphic(true);
        tabPane.setTabMinWidth(30.0D);
        tabPane.setTabMaxWidth(30.0D);
        tabPane.setTabMinHeight(200.0D);
        tabPane.setTabMaxHeight(200.0D);

        // tabs in the main UI
        Tab checkoutTab = TabBuilder.buildTab("Checkout");
        Tab analyticsTab = TabBuilder.buildTab("Analytics");
        Tab inventory = TabBuilder.buildTab("Inventory");
        Tab users = TabBuilder.buildTab("Users");

        // create the inventory view (custom javaFX layout container ex. HBox, VBox)
        Inventory inventoryView = new Inventory();
        // set the custom view as the content of the tab created for inventory (inventory)
        inventory.setContent(inventoryView);

        // The checkout Section
        Checkout checkoutLayout = new Checkout();
        BorderPane checkoutContainer = checkoutLayout.getLayout();
        checkoutTab.setContent(checkoutContainer);

        InventoryManagementApplicationController.NavigationHandler handler = new InventoryManagementApplicationController.NavigationHandler() {
            @Override
            public void goToInventory() {
                navigate("Inventory");
            }

            public void navigate(String destination) {
                if (destination.equals("Inventory")) {
                    tabPane.getSelectionModel().select(inventory);
                }
            }
        };


        // The Stock Section
        Analytics analyticsView = new Analytics(handler);
        VBox analyticsViewContainer = analyticsView.getLayout();

        ScrollPane scrollableAnalytics = new ScrollPane(analyticsViewContainer);
        scrollableAnalytics.setFitToWidth(true);
        scrollableAnalytics.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        analyticsTab.setContent(scrollableAnalytics);

        Users userTabView = new Users();
        users.setContent(userTabView);

        ThemeObserver.init().addObserver(inventoryView);
        ThemeObserver.init().addObserver(analyticsView);
        ThemeObserver.init().addObserver(checkoutLayout);
        ThemeObserver.init().applyDarkThemeChange();

        // Add tabs to the tabPane
        tabPane.getTabs().addAll(
                checkoutTab,
                analyticsTab,
                inventory,
                users
        );

        rootContainer.getChildren().addAll(titleBar, tabPane);

        Scene scene = new Scene(rootContainer);

        scene.getStylesheets().add(
            String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        // remove the frame of the window
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}