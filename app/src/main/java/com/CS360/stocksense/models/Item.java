package com.CS360.stocksense.models;

public class Item {
    private Integer id; // Supabase-generated ID
    private String item_name;
    private int quantity;
    private String location;
    private int alert_level;
    private String organization_name;
    private String database_id;
    private String database_name;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return item_name;
    }

    public void setItemName(String item_name) {
        this.item_name = item_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAlertLevel() {
        return alert_level;
    }

    public void setAlertLevel(int alert_level) {
        this.alert_level = alert_level;
    }

    public String getOrganizationName() {
        return organization_name;
    }

    public void setOrganizationName(String organization_name) {
        this.organization_name = organization_name;
    }
    public String getDatabasenName(){return database_name;}
    public void setDatabaseName(String database_name){
        this.database_name = database_name;
    }
    public String getDatabaseId() {
        return database_id;
    }
    public void setDatabaseId(String database_id) {
        this.database_id = database_id;
    }
}
