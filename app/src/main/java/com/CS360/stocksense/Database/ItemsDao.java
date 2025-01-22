package com.CS360.stocksense.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao // Data Access Object for Items
public interface ItemsDao {

    @Insert // Insert a new item
    void insert(Items item);

    @Update // Update an existing item
    void update(Items item);

    @Delete // Delete an item
    void delete(Items item);

    @Query("SELECT * FROM items") // Get all items
    List<Items> getAllItems();

    @Query("SELECT * FROM items WHERE id = :id") // Get item by id
    Items getItemById(int id);

    @Query("SELECT (quantity < alertLevel) FROM items WHERE id = :itemId") // Check if item has low inventory
    boolean isLowInventory(int itemId);

    @Query("UPDATE items SET alertLevel = :alertLevel WHERE id = :id") // Update alert level of an item
    void updateAlertLevel(int id, int alertLevel);
}
