package com.example.inventorymanagementsystem.state;

import com.example.inventorymanagementsystem.InventoryManagementApplication;

import java.net.URL;

public class Constants {
    public static final String DARK_THEME_CSS = String.valueOf(
            InventoryManagementApplication.class.getResource("css/darkTheme.css"));

    public static final String LIGHT_THEME_CSS = String.valueOf(
            InventoryManagementApplication.class.getResource("css/lightTheme.css"));
    public static final URL IMAGE_UNAVAILABLE = InventoryManagementApplication.class.getResource("images/ChatGPT Image Jun 1, 2025, 07_19_47 PM.png");
    public static final String usersMediaDirectory = "media\\users";
    public static final String customersMediaDirectory = "media\\customers";
    public static final String itemsMediaDirectory = "media\\products";
    public static final String CURRENCY_UNIT = "Rs.";
    public static final int MAX_USERS = 10;
    public static final int MAX_ITEMS = 500;

}
