package com.example.inventorymanagementsystem.services.interfaces;

import com.example.inventorymanagementsystem.models.ItemDetail;

public interface TableContainerInterface<T> {
    void addItem();
    void refresh();
    void update(T itemDetail);
    void delete(T itemDetail);

    void onSelectItem(T item);
}
