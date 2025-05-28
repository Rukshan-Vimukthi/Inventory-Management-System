package com.example.inventorymanagementsystem.state;

import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;

public class ThemeObserver {
    private static ThemeObserver themeObserver;
    private static List<com.example.inventorymanagementsystem.services.interfaces.ThemeObserver> nodes;
    private static List<com.example.inventorymanagementsystem.services.interfaces.ThemeObserver> controls;

    private ThemeObserver(){
        nodes = new ArrayList<>();
        controls = new ArrayList<>();
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
        for (var item : nodes){
            item.lightTheme();
        }
    }

    public void applyDarkThemeChange(){
        for (var item : nodes){
            item.darkTheme();
        }
    }
}
