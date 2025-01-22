package com.CS360.stocksense.models;

public class DatabaseSelection {
    private String databaseId;
    private String databaseName;

    public DatabaseSelection(String databaseId, String databaseName) {
        this.databaseId = databaseId;
        this.databaseName = databaseName;
    }

    public String getId() {
        return databaseId;
    }

    public void setId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getName() {
        return databaseName;
    }

    public void setName(String databaseName) {
        this.databaseName = databaseName;
    }
}
