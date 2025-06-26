package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.SettingsData;
import com.example.inventorymanagementsystem.state.SettingsManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.prefs.Preferences;

import java.sql.SQLException;

public class Settings extends VBox implements ThemeObserver{

    Text errorMessage;

    public Settings()  throws SQLException  {
        super();

        VBox settingsManinContainer = new VBox();
        settingsManinContainer.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        Preferences prefs = Preferences.userRoot().node("inventorySettings");
        String savedTheme = prefs.get("theme", "Light"); // default Light if not saved

        if ("Dark".equals(savedTheme)) {
            com.example.inventorymanagementsystem.state.ThemeObserver.init().applyDarkThemeChange();
        } else {
            com.example.inventorymanagementsystem.state.ThemeObserver.init().applyLightThemeChange();
        }

        VBox headerSection = new VBox();
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setSpacing(8);
        headerSection.setPadding(new Insets(9, 0, 20, 0));

        Text title = new Text("System Settings");
        title.getStyleClass().add("heading-texts");
        title.setTextAlignment(TextAlignment.CENTER);

        Text subtitle = new Text("Customize your experience and inventory thresholds");
        subtitle.getStyleClass().add("paragraph-texts");

        headerSection.getChildren().addAll(title, subtitle);

        VBox themeSettingsBox = new VBox(10);
        themeSettingsBox.setPadding(new Insets(10));
        themeSettingsBox.getStyleClass().add("settings-containers");

        Text themeLabel = new Text("Default Theme:");

        HBox themeSelectContainer = new HBox();
        themeSelectContainer.setSpacing(9);
        themeLabel.getStyleClass().add("paragraph-texts");
        ComboBox<String> themeSelector = new ComboBox<>();
        themeSelector.getStyleClass().add("default-dropdowns");
        themeSelector.getItems().addAll("Light", "Dark");
        themeSelector.setValue(savedTheme);

        Button applyThemeButton = new Button("Apply Theme");
        applyThemeButton.getStyleClass().add("add-button");
        applyThemeButton.setOnAction(e -> {
            String selected = themeSelector.getValue();

            prefs.put("theme", selected);

            if ("Dark".equals(selected)) {
                com.example.inventorymanagementsystem.state.ThemeObserver.init().applyDarkThemeChange();
            } else {
                com.example.inventorymanagementsystem.state.ThemeObserver.init().applyLightThemeChange();
            }
        });


        themeSelectContainer.getChildren().addAll(themeSelector, applyThemeButton);
        themeSettingsBox.getChildren().addAll(themeLabel, themeSelectContainer);

        VBox stockSettingsBox = new VBox();
        stockSettingsBox.setPadding(new Insets(10));
        stockSettingsBox.setSpacing(9);
        stockSettingsBox.getStyleClass().add("settings-containers");

        Text stockScopeLabel = new Text("Apply Target Stock To:");
        stockScopeLabel.getStyleClass().add("normal-texts");
        ComboBox<String> stockScopeSelector = new ComboBox<>();
        stockScopeSelector.getItems().addAll("All Items", "Specific Item");
        stockScopeSelector.setValue("All Items");
        stockScopeSelector.getStyleClass().add("default-dropdowns");

        HBox itemInputField = new HBox();
        itemInputField.setSpacing(9);
        itemInputField.setPrefHeight(15);
        itemInputField.setMaxHeight(15);

        TextField itemIdInput = new TextField();
        itemIdInput.setPromptText("Enter Item ID here");
        itemIdInput.setPrefWidth(160);
        itemIdInput.setMaxWidth(160);
        itemIdInput.setPrefHeight(15);
        itemIdInput.setMaxHeight(15);
        itemIdInput.getStyleClass().add("default-text-areas");
        itemInputField.getChildren().addAll(stockScopeSelector, itemIdInput);

        Text stockLabel = new Text("Target Stock Amount: ");
        stockLabel.getStyleClass().add("paragraph-texts");
        Spinner<Integer> stockTargetSpinner  = new Spinner<>(1, 1000, 50);

        CheckBox lowStockAlert = new CheckBox("Enable Low Stock Alerts");
        lowStockAlert.getStyleClass().add("normal-checkboxes-styles");
        CheckBox overStockAlert = new CheckBox("Enable Over Stock Alerts");
        overStockAlert.getStyleClass().add("normal-checkboxes-styles");
        lowStockAlert.setSelected(true);
        overStockAlert.setSelected(true);

        Label info = new Label("Items below target will be shown as low stock. Items significantly above will be flagged as overstock.");
        info.setWrapText(true);
        info.getStyleClass().add("normal-labels");

        Button saveStockSettingsButton = new Button("Save Stock Settings");
        saveStockSettingsButton.getStyleClass().add("add-button");

        saveStockSettingsButton.setOnAction(e -> {
            SettingsData settings = SettingsManager.getSettings();

            String selectedScope = stockScopeSelector.getValue();
            int targetAmount = stockTargetSpinner.getValue();

            if ("All Items".equals(selectedScope)) {
                settings.setLowStockLimit(targetAmount);
            } else {
                String itemIdText = itemIdInput.getText().trim();
                if (itemIdText.isEmpty()) {
                    errorMessage.setText("Please enter an item ID.");
                    return;
                }

                try {
                    int itemId = Integer.parseInt(itemIdText);
                    settings.setItemTarget(itemId, targetAmount);
                } catch (NumberFormatException ex) {
                    errorMessage.setText("Item ID must be a valid number.");
                    return;
                }
            }

            SettingsManager.saveSettings();

            errorMessage.setText("Stock settings saved successfully.");
        });


        stockSettingsBox.getChildren().addAll( stockScopeLabel, itemInputField, stockLabel, stockTargetSpinner, lowStockAlert, overStockAlert, info, saveStockSettingsButton );

        VBox summaryPanel = new VBox(5);
        summaryPanel.getStyleClass().add("settings-containers");
        Text summaryTitle = new Text("Current Settings Summary:");
        summaryTitle.getStyleClass().add("normal-texts");
        summaryTitle.setStyle("-fx-font-weight: bold;");

        Text currentTheme = new Text("Current Theme: Light");
        currentTheme.getStyleClass().add("paragraph-texts");
        Text currentThreshold = new Text("Stock Threshold: " );
        currentThreshold.getStyleClass().add("paragraph-texts");
        Text currentAlerts = new Text("Alerts: Enabled");
        currentAlerts.getStyleClass().add("paragraph-texts");

        summaryPanel.getChildren().addAll(summaryTitle, currentTheme, currentThreshold, currentAlerts);

        HBox footerButtons = new HBox(10);
        footerButtons.setPadding(new Insets(0, 0, 0, 10));
        Button saveAll = new Button("Save All Settings");
        saveAll.getStyleClass().add("add-button");
        Button reset = new Button("Reset to Defaults");
        reset.getStyleClass().add("primary-button");
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("delete-button");

        VBox errorMessageContainer = new VBox();
        errorMessageContainer.setPadding(new Insets(30, 0, 0, 10));
        errorMessage = new Text("");
        errorMessage.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #ef4444; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10 14; -fx-text-fill: #b91c1c; -fx-font-size: 13px; -fx-font-weight: 500;");
        errorMessageContainer.getChildren().addAll(errorMessage);

        footerButtons.getChildren().addAll(saveAll, reset, cancel);

        getChildren().addAll(headerSection, themeSettingsBox, stockSettingsBox, summaryPanel, footerButtons, errorMessageContainer);

    }
    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: white;");
        this.getStylesheets().clear();
        this.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/lightTheme.css"))
        );
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #222;");
        this.getStylesheets().clear();
        this.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/darkTheme.css"))
        );
    }
}
