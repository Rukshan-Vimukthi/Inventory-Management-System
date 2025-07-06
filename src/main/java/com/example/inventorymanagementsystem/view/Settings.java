package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Data;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class Settings extends VBox implements ThemeObserver {

    private Text errorMessage;
    private TextField lowStockTargetInput;
    private TextField overStockTargetInput;
    private ComboBox<String> themeSelector;

    public Settings() throws SQLException {
        super();

        VBox settingsMainContainer = new VBox();
        settingsMainContainer.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        Preferences prefs = Preferences.userRoot().node("inventorySettings");
        String savedTheme = prefs.get("theme", "Light");

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
        themeLabel.getStyleClass().add("paragraph-texts");

        HBox themeSelectContainer = new HBox(10);
        themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll("Light", "Dark");
        themeSelector.setValue(savedTheme);
        themeSelector.setPrefWidth(200);
        themeSelector.getStyleClass().add("default-dropdowns");

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

        VBox stockSettingsBox = new VBox(10);
        stockSettingsBox.setPadding(new Insets(10));
        stockSettingsBox.getStyleClass().add("settings-containers");

        Text stockScopeLabel = new Text("Apply Target Stock To:");
        stockScopeLabel.getStyleClass().add("normal-texts");

        ComboBox<String> stockScopeSelector = new ComboBox<>();
        stockScopeSelector.getItems().addAll("All Items", "Specific Item");
        stockScopeSelector.setValue("All Items");
        stockScopeSelector.getStyleClass().add("default-dropdowns");
        stockScopeSelector.setPrefWidth(200);

        ComboBox<ItemDetail> itemComboBox = new ComboBox<>();
        itemComboBox.setItems(Data.getInstance().getItemDetails());
        itemComboBox.setPrefWidth(200);
        itemComboBox.getStyleClass().add("default-dropdowns");
        itemComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ItemDetail item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSize() + ")");
                }
            }
        });

        VBox specificItemBox = new VBox(10, itemComboBox);
        specificItemBox.setVisible(false);

        stockScopeSelector.setOnAction(ev -> {
            specificItemBox.setVisible("Specific Item".equals(stockScopeSelector.getValue()));
        });

        lowStockTargetInput = new TextField();
        lowStockTargetInput.setPromptText("Low Stock Target");
        lowStockTargetInput.getStyleClass().add("default-text-areas");
        lowStockTargetInput.setPrefWidth(200);
        lowStockTargetInput.setMaxWidth(200);

        overStockTargetInput = new TextField();
        overStockTargetInput.setPromptText("Over Stock Target");
        overStockTargetInput.getStyleClass().add("default-text-areas");
        overStockTargetInput.setPrefWidth(200);
        overStockTargetInput.setMaxWidth(200);

        Button saveStockSettingsButton = new Button("Save Stock Settings");
        saveStockSettingsButton.getStyleClass().add("add-button");

        errorMessage = new Text();
        errorMessage.getStyleClass().add("normal-texts");

        saveStockSettingsButton.setOnAction(e -> {
            var settings = com.example.inventorymanagementsystem.services.utils.Settings.getInstance();
            String scope = stockScopeSelector.getValue();
            try {
                String lowInput = lowStockTargetInput.getText().trim();
                String overInput = overStockTargetInput.getText().trim();

                if (lowInput.isEmpty() || overInput.isEmpty()) {
                    showMessage("❌ Please fill both fields.", false);
                    return;
                }

                int low = Integer.parseInt(lowInput);
                int over = Integer.parseInt(overInput);

                if ("All Items".equals(scope)) {
                    settings.setLowStockLimit(low);
                    settings.setOverStockLimit(over);
                } else {
                    ItemDetail item = itemComboBox.getValue();
                    if (item == null) {
                        showMessage("❌ Please select an item.", false);
                        return;
                    }
                    settings.setItemTarget(item.getId(), low);
                }

                settings.saveSettings();
                showMessage("✅ Stock settings saved successfully.", true);
            } catch (NumberFormatException ex) {
                showMessage("❌ Enter valid numbers.", false);
            }
        });

        Text actionInfo = new Text("You can reset to default settings or cancel to return to the analytics section.");
        actionInfo.getStyleClass().add("normal-texts");

        Button reset = new Button("Reset to Defaults");
        reset.getStyleClass().add("primary-button");
        reset.setOnAction(e -> {
            var settings = com.example.inventorymanagementsystem.services.utils.Settings.getInstance();
            settings.resetToDefaultSettings(new File("settings.json"));
            settings.loadSettings(new File("settings.json"));

            lowStockTargetInput.setText("10");
            overStockTargetInput.setText("100");
            themeSelector.setValue("Dark");
            prefs.put("theme", "Dark");
            com.example.inventorymanagementsystem.state.ThemeObserver.init().applyDarkThemeChange();

            showMessage("🔁 Settings reset to default.", true);
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("delete-button");
        cancel.setOnAction(e -> {
            try {
                Parent analyticsView = FXMLLoader.load(getClass().getResource("/com/example/inventorymanagementsystem/views/Analytics.fxml"));
                Stage stage = (Stage) cancel.getScene().getWindow();
                stage.getScene().setRoot(analyticsView);
            } catch (IOException ex) {
                showMessage("❌ Could not navigate to analytics.", false);
            }
        });
        VBox footerContainer = new VBox();
        footerContainer.setPadding(new Insets(4, 0, 0, 10));
        footerContainer.setSpacing(3);
        HBox footerButtons = new HBox(10, reset, cancel);
        footerButtons.setPadding(new Insets(10, 10, 10, 0));

        stockSettingsBox.getChildren().addAll(
                stockScopeLabel, stockScopeSelector, specificItemBox,
                lowStockTargetInput, overStockTargetInput,
                saveStockSettingsButton, errorMessage
        );
        footerContainer.getChildren().addAll(actionInfo, footerButtons);

        getChildren().addAll(headerSection, themeSettingsBox, stockSettingsBox, footerContainer);
    }

    private void showMessage(String msg, boolean isSuccess) {
        errorMessage.setText(msg);
        errorMessage.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(ev -> errorMessage.setText(""));
        pause.play();
    }

    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: white;");
        this.getStylesheets().clear();
        this.getStylesheets().add(String.valueOf(InventoryManagementApplication.class.getResource("css/lightTheme.css")));
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #222;");
        this.getStylesheets().clear();
        this.getStylesheets().add(String.valueOf(InventoryManagementApplication.class.getResource("css/darkTheme.css")));
    }
}