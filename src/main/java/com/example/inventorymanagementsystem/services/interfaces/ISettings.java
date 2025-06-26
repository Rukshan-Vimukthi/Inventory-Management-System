package com.example.inventorymanagementsystem.services.interfaces;

import java.io.File;

public interface ISettings {
    void setLowStockLimit(int limit);
    void saveSettings();
    void loadSettings(File settingsFile);
    void resetToDefaultSettings();
}
