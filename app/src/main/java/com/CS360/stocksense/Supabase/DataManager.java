package com.CS360.stocksense.Supabase;

import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private final SupabaseRepository repository;
    private List<Item> items = new ArrayList<>();
    private List<DatabaseSelection> databases = new ArrayList<>();

    public DataManager() {
        repository = new SupabaseRepository();
    }

    //Used to populate Database recycler
    public void fetchOrganization(String organizationName, DataCallback<List<DatabaseSelection>> callback) {
        repository.fetchOrganization(organizationName, new DataCallback<List<DatabaseSelection>>() {
            @Override
            public void onSuccess(List<DatabaseSelection> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void fetchDatabase(String organizationName, String databaseId, DataCallback<List<Item>> callback) {
        repository.fetchDatabase(organizationName, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                callback.onSuccess(result); // Forward the list of items to the caller
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Forward the error to the caller
            }
        });
    }

    public void deleteDatabase(String databaseId, DataCallback<Void> callback) {
        SupabaseRepository repository = new SupabaseRepository();
        repository.deleteDatabase(databaseId, callback);
    }
    public void fetchSingleItem(String organizationName, String itemId, String databaseId, DataCallback<Item> callback) {
        repository.readItem(organizationName, itemId, databaseId, new DataCallback<Item>() {
            @Override
            public void onSuccess(Item result) {
                callback.onSuccess(result); // Forward the item to the caller
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Forward the error to the caller
            }
        });
    }

    // Create items
    public void createItems(String organizationName, List<Item> items, String databaseId, DataCallback<List<Item>> callback) {
        repository.createItem(organizationName, items, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                callback.onSuccess(result); // Pass the created items back to the caller
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Pass the error back to the caller
            }
        });
    }

    // Update an item
    public void updateItem(String organizationName, Item item, String databaseId, DataCallback<List<Item>> callback) {
        repository.updateItem(organizationName, item, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                callback.onSuccess(result); // Forward the updated item back to the caller
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Forward the error to the caller
            }
        });
    }

    // Delete an item
    public void deleteItem(String organizationName, String itemId, String databaseId, DataCallback<Void> callback) {
        repository.deleteItem(organizationName, itemId, databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(null); // Confirm successful deletion
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Forward the error to the caller
            }
        });
    }




}
