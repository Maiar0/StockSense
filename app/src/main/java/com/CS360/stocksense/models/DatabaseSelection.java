package com.CS360.stocksense.models;

import androidx.annotation.NonNull;

/**
 * Represents a database selection in the StockSense application.
 * <p>
 * This class is used to encapsulate the details of a database, including its unique identifier
 * (`database_id`) and its human-readable name (`database_name`). It provides getters and setters
 * for accessing and modifying these fields.
 * <p>
 * Purpose:
 * - To serve as a model for storing and transferring database selection information.
 * <p>
 * Example Usage:
 * ```java
 * DatabaseSelection dbSelection = new DatabaseSelection("db001", "InventoryDB");
 * System.out.println("Database ID: " + dbSelection.getId());
 * System.out.println("Database Name: " + dbSelection.getName());
 * ```
 * <p>
 * Notes:
 * - The field names match the database schema for compatibility.
 * - The class is primarily used in conjunction with database querying and selection features.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class DatabaseSelection {
    private String database_id;
    private String database_name;
    /**
     * Constructs a new DatabaseSelection instance with the specified ID and name.
     *
     * @param database_id   The unique identifier for the database.
     * @param database_name The human-readable name of the database.
     */
    public DatabaseSelection(String database_id, String database_name) {
        this.database_id = database_id;
        this.database_name = database_name;
    }
    /**
     * Gets the database ID.
     *
     * @return The unique identifier of the database.
     */
    public String getId() {
        return database_id;
    }
    /**
     * Sets the database ID.
     *
     * @param databaseId The unique identifier to assign to the database.
     */
    public void setId(String databaseId) {
        this.database_id = databaseId;
    }
    /**
     * Gets the database name.
     *
     * @return The human-readable name of the database.
     */
    public String getName() {
        return database_name;
    }
    /**
     * Sets the database name.
     *
     * @param databaseName The human-readable name to assign to the database.
     */
    public void setName(String databaseName) {
        this.database_name = databaseName;
    }
    /**
     * Returns a string representation of the DatabaseSelection.
     *
     * @return A string containing the database ID and name.
     */
    @NonNull
    @Override
    public String toString() {
        return "DatabaseSelection{" +
                "database_id='" + database_id + '\'' +
                ", database_name='" + database_name + '\'' +
                '}';
    }
}
