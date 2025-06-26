package com.example.inventorymanagementsystem.state;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsManager {
    private static final String SETTINGS_FILE = "settings.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static SettingsData settingsData;

    public static SettingsData getSettings() {
        if (settingsData == null) loadSettings();
        return settingsData;
    }

    public static void loadSettings() {
        try {
            if (Files.exists(Paths.get(SETTINGS_FILE))) {
                Reader reader = new FileReader(SETTINGS_FILE);
                settingsData = gson.fromJson(reader, SettingsData.class);
                reader.close();
            } else {
                settingsData = new SettingsData();
                saveSettings();
            }
        } catch (IOException e) {
            e.printStackTrace();
            settingsData = new SettingsData();
        }
    }

    public static void saveSettings() {
        try (Writer writer = new FileWriter(SETTINGS_FILE)) {
            gson.toJson(settingsData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
