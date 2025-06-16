package com.example.inventorymanagementsystem.services.interfaces;

import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.scene.control.Control;

import java.sql.SQLException;
import java.util.List;

public interface TableContainerInterface<T> {
    void addItem();
    void refresh();
    void update(T itemDetail);
    void delete(T itemDetail);
    void onSelectItem(T item) throws SQLException;
    void onSearch(List<FormField<? extends Control, ?>> formFields, String searchBoxText);
}
