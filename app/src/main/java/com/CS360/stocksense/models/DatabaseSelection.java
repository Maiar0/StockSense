package com.CS360.stocksense.models;

public class DatabaseSelection {
    private String database_id;
    private String database_name;

    public DatabaseSelection(String database_id, String database_name) {
        this.database_id = database_id;
        this.database_name = database_name;
    }

    public String getId() {
        return database_id;
    }

    public void setId(String databaseId) {
        this.database_id = databaseId;
    }

    public String getName() {
        return database_name;
    }

    public void setName(String databaseName) {
        this.database_name = databaseName;
    }
}
