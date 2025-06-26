package com.example.inventorymanagementsystem.services.utils;

import com.example.inventorymanagementsystem.services.interfaces.ISettings;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.SettingsData;

import java.io.File;
import java.io.IOException;

public class Settings implements ISettings {
    private static Settings settings;
    private SettingsData settingData;
    private Settings(){
        initSettingsFile();
    }

    private void initSettingsFile(){
        File applicationDir = new File(Constants.APPLICATION_DIR);
        if (!applicationDir.exists()){
            boolean dirCreated = applicationDir.mkdirs();
        }

        File settingsFile = new File(Constants.APPLICATION_DIR + "\\settings.json");
        if (!settingsFile.exists()){
            try {
                boolean fileCreated = settingsFile.createNewFile();
                resetToDefaultSettings();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        loadSettings(settingsFile);
    }

    public static Settings getInstance(){
        if (settings == null){
            settings = new Settings();
        }
        return settings;
    }

    @Override
    public void setLowStockLimit(int limit) {

    }

    @Override
    public void saveSettings() {

    }

    @Override
    public void loadSettings(File settingsFile) {

    }

    @Override
    public void resetToDefaultSettings() {

    }
}
