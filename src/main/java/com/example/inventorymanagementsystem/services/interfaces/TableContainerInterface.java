package com.example.inventorymanagementsystem.services.interfaces;

import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.scene.control.Control;

import java.util.List;

public interface TableContainerInterface<T> {
    void addItem();
    void refresh();
    void update(T itemDetail);
    void delete(T itemDetail);
    void onSelectItem(T item);
    void onSearch(List<FormField<? extends Control, ?>> formFields, String searchBoxText);
}
