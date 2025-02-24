package com.CS360.stocksense.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Represents an item in the StockSense database.
 * <p>
 * This model is used for querying and managing item data within the database.
 * It contains fields such as item ID, name, quantity, location, alert level,
 * and associated metadata like organization name, database ID, and database name.
 * <p>
 * Purpose:
 * - Provides a structure to encapsulate item data.
 * - Offers getters and setters for accessing and modifying item properties.
 * <p>
 * Note:
 * - Field names are designed to match database schema for compatibility.
 * - Validation logic ensures data integrity where applicable.
 * <p>
 * Example Usage:
 * ```java
 * Item item = new Item();
 * item.setItemId("12345");
 * item.setItemName("Widget");
 * item.setQuantity(10);
 * System.out.println(item);
 * ```
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */

public class Item implements Serializable {
    private String item_id;
    private String item_name;
    private int quantity;
    private String location;
    private int alert_level;
    private String organization_id;
    private String database_id;
    private String database_name;


    /**
     * Gets the item's unique ID.
     *
     * @return The unique ID of the item.
     */
    public String getItemId() {
        return item_id;
    }
    /**
     * Sets the item's unique ID.
     *
     * @param item_id The unique ID to assign to the item.
     */
    public void setItemId(String item_id) {
        this.item_id = item_id;
    }
    /**
     * Gets the item's name.
     *
     * @return The name of the item.
     */
    public String getItemName() {
        return item_name;
    }
    /**
     * Sets the item's name.
     *
     * @param item_name The name to assign to the item.
     */
    public void setItemName(String item_name) {
        this.item_name = item_name;
    }
    /**
     * Gets the quantity of the item.
     *
     * @return The quantity of the item.
     */
    public int getQuantity() {
        return quantity;
    }
    /**
     * Sets the quantity of the item. Quantity must be non-negative.
     * Negative values will be discarded without change
     * @param quantity The quantity to assign to the item.
     */
    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }
    /**
     * Gets the item's location.
     *
     * @return The location of the item.
     */
    public String getLocation() {
        return location;
    }
    /**
     * Sets the item's location.
     *
     * @param location The location to assign to the item.
     */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * Gets the item's alert level.
     *
     * @return The alert level of the item.
     */
    public int getAlertLevel() {
        return alert_level;
    }
    /**
     * Sets the item's alert level. Alert level must be non-negative.
     * Negative values will be discarded without change
     * @param alert_level The alert level to assign to the item.
     */
    public void setAlertLevel(int alert_level) {
        if (alert_level >= 0) {
            this.alert_level = alert_level;
        }
    }
    /**
     * Gets the organization's name associated with the item.
     *
     * @return The organization name.
     */
    public String getOrganizationId() {
        return organization_id;
    }
    /**
     * Sets the organization's name associated with the item.
     *
     * @param organization_id The organization name to assign to the item.
     */
    public void setOrganizationId(String organization_id) {
        this.organization_id = organization_id;
    }
    /**
     * Gets the name of the database containing the item.
     *
     * @return The database name.
     */
    public String getDatabaseName(){return database_name;}
    /**
     * Sets the name of the database containing the item.
     *
     * @param database_name The database name to assign to the item.
     */
    public void setDatabaseName(String database_name){
        this.database_name = database_name;
    }
    /**
     * Gets the database ID associated with the item.
     *
     * @return The database ID.
     */
    public String getDatabaseId() {
        return database_id;
    }
    /**
     * Sets the database ID associated with the item.
     *
     * @param database_id The database ID to assign to the item.
     */
    public void setDatabaseId(String database_id) {
        this.database_id = database_id;
    }
    /**
     * Returns a string representation of the item.
     *
     * @return A string with the item's details.
     */
    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "item_id='" + item_id + '\'' +
                ", item_name='" + item_name + '\'' +
                ", quantity=" + quantity +
                ", location='" + location + '\'' +
                ", alert_level=" + alert_level +
                ", organization_name='" + organization_id + '\'' +
                ", database_id='" + database_id + '\'' +
                ", database_name='" + database_name + '\'' +
                '}';
    }
    /**
     *  Allows for increment or decrement of quantity
     * May reduce quantity but not below zero
     * @param change The value to change quantity by.
     */
    public void updateQuantity(int change) {
        change += quantity;
        if(change >= 0) {
            this.quantity = change;
        }
    }
}
