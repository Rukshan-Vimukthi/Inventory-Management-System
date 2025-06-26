package com.example.inventorymanagementsystem.services.utils;

import com.example.inventorymanagementsystem.services.interfaces.ISettings;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.SettingsData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Map;

public class Settings implements ISettings {
    private static Settings settings;
    private SettingsData settingData;

    private File settingsFile;
    private Gson gson;
    private Settings(){
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        initSettingsFile();
    }

    /**
     * initialize the settings file. prepare everything from creating the dir and file if it does not exist and write
     * the default settings if it does not exist
     */
    private void initSettingsFile(){
        File applicationDir = new File(Constants.APPLICATION_DIR);
        if (!applicationDir.exists()){
            boolean dirCreated = applicationDir.mkdirs();
        }

        settingsFile = new File(Constants.APPLICATION_DIR + "\\settings.json");
        if (!settingsFile.exists()){
            try {
                boolean fileCreated = settingsFile.createNewFile();
                resetToDefaultSettings(settingsFile);
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
        if (settingData != null){
            settingData.setLowStockLimit(limit);
        }
    }

    public int getLowStockLimit() {
        return settingData.getLowStockLimit();
    }

    @Override
    public void saveSettings() {
        String jsonContent = gson.toJson(settingData);
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(settingsFile))){
            bufferedWriter.write(jsonContent);
        }catch(IOException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void loadSettings(File settingsFile) {
        try(BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(settingsFile)))){
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            System.out.println("Loading data from json file");
            System.out.println(stringBuilder.toString());
            settingData = gson.fromJson(stringBuilder.toString(), SettingsData.class);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void resetToDefaultSettings(File settingsFile) {
        settingData = new SettingsData();
        String jsonContent = gson.toJson(settingData);
        System.out.println("Writing default data");
        System.out.println(jsonContent);
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(settingsFile))){
            bufferedWriter.write(jsonContent);
        }catch(IOException exception){
            exception.printStackTrace();
        }
    }

    public void setOverStockLimit(int overStockLimit) {
        settingData.setOverStockLimit(overStockLimit);
    }
    public void setItemTarget(int itemId, int target) {
        settingData.setItemTarget(itemId, target);
    }
    public Integer getItemTarget(int itemId) {
        return settingData.getItemTarget(itemId); // may return null
    }
    public Map<Integer, Integer> getAllItemTargets() {
        return settingData.getAllItemTargets();
    }

    public int getOverStockLimit() {return settingData.getOverStockLimit();}

//    public void setOverStockLimit(int overStockLimit) {settingData.setOverStockLimit(overStockLimit);}
}
