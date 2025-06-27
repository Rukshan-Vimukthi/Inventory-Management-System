package com.example.inventorymanagementsystem.state;

import java.util.HashMap;
import java.util.Map;

public class SettingsData {
    public Integer lowStockLimit;
    public Integer overStockLimit;
    public boolean isDark;


//    private Map<Integer, Integer> itemSpecificTargets;

    /**
     * Assign default values for all variables in the constructor
     */
    public SettingsData(){
        lowStockLimit = 10;
        overStockLimit = 100;
        isDark = true;
//        itemSpecificTargets = new HashMap<>();
    }

    public int getLowStockLimit() {
        return lowStockLimit;
    }

    public void setLowStockLimit(int lowStockLimit) {
        this.lowStockLimit = lowStockLimit;
    }

    public int getOverStockLimit() {
        return overStockLimit;
    }

    public void setOverStockLimit(int overStockLimit) {
        this.overStockLimit = overStockLimit;
    }

//    public void setItemTarget(int itemId, int target) {
//        itemSpecificTargets.put(itemId, target);
//    }

//    public Integer getItemTarget(int itemId) {
//        return itemSpecificTargets.get(itemId); // may return null
//    }

//    public Map<Integer, Integer> getAllItemTargets() {
//        return itemSpecificTargets;
//    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
    }

}
