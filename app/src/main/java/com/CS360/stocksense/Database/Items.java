package com.CS360.stocksense.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items") // Define items table
public class Items {

    @PrimaryKey
    private int id;

    private String itemName;
    private int quantity;
    private String location;
    private int alertLevel;
    private long lastAlertTimestamp;

    // Constructor
    public Items(int id, String itemName, int quantity, String location, int alertLevel) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.location = location;
        this.alertLevel = alertLevel;
        lastAlertTimestamp = 0;
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter for itemName
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    // Getter and setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void updateQuantity(int quantity){
        this.quantity = this.quantity + quantity;
    }
    // Getter and setter for location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter and setter for alertLevel
    public int getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }

    // Getter and setter for lastAlertTimestamp
    public long getLastAlertTimestamp() {
        return lastAlertTimestamp;
    }

    public void setLastAlertTimestamp(long lastAlertTimestamp) {
        this.lastAlertTimestamp = lastAlertTimestamp;
    }

    // Check if inventory is low
    public boolean isLowInventory() {
        return this.quantity < this.alertLevel;
    }
}
