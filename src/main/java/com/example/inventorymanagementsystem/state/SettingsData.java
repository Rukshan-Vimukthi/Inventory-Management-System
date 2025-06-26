package com.example.inventorymanagementsystem.state;

import java.util.HashMap;
import java.util.Map;

public class SettingsData {
    public Integer lowStockLimit = 10;
    public Integer overStockLimit = 100;
    public Map<Integer, Integer> itemSpecificTargets = new HashMap<>();

    public int getLowStockLimit() {
        return lowStockLimit;
    }

    public void setLowStockLimit(int lowStockLimit) {
        this.lowStockLimit = lowStockLimit;
    }

    public int getOverStockLimit() {return overStockLimit;}

    public void setOverStockLimit(int overStockLimit) {this.overStockLimit = overStockLimit;}

    public void setItemTarget(int itemId, int target) {
        itemSpecificTargets.put(itemId, target);
    }

    public Integer getItemTarget(int itemId) {
        return itemSpecificTargets.get(itemId); // may return null
    }

    public Map<Integer, Integer> getAllItemTargets() {
        return itemSpecificTargets;
    }
}
