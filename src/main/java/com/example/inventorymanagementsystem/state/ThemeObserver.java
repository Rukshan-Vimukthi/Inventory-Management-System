package com.example.inventorymanagementsystem.state;

import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;

public class ThemeObserver {
    private static ThemeObserver themeObserver;
    private static List<com.example.inventorymanagementsystem.services.interfaces.ThemeObserver> nodes;

    int currentTheme = 0;

    private ThemeObserver(){
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
    }

    public static ThemeObserver init(){
        if (themeObserver == null){
            themeObserver = new ThemeObserver();
        }
        return themeObserver;
    }

    public void addObserver(com.example.inventorymanagementsystem.services.interfaces.ThemeObserver observer){
        nodes.add(observer);
    }

    public void applyLightThemeChange(){
        currentTheme = 1;
        for (var item : nodes){
            if (item != null) {
                item.lightTheme();
            }
        }
    }

    public void applyDarkThemeChange(){
        currentTheme = 0;
        for (var item : nodes){
            if (item != null) {
                item.darkTheme();
            }
        }
    }

    public void notifyAllListeners(){
        if (currentTheme == 0){
            applyDarkThemeChange();
        }else{
            applyLightThemeChange();
        }
    }
}
