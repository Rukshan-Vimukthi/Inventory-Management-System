package com.example.inventorymanagementsystem.services.interfaces;

import com.example.inventorymanagementsystem.models.ItemDetail;

import java.sql.SQLException;

public interface ItemPreviewObserver {
    void update(ItemDetail itemDetail) throws SQLException;
}
