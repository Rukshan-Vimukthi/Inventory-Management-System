package com.example.inventorymanagementsystem.view.components;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CurrencyCellFactory {

    // Reusable method that accepts a custom string prefix (like "$", "€", etc.)
    public static <T, V> Callback<TableColumn<T, V>, TableCell<T, V>> withPrefix(String prefix) {
        return column -> new TableCell<T, V>() {
            @Override
            protected void updateItem(V item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(prefix + item.toString());
                }
            }
        };
    }
}