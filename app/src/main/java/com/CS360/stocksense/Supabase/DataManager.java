package com.CS360.stocksense.Supabase;

import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.util.List;

public class DataManager {
    private final SupabaseRepository repository;

    public DataManager() {
        repository = new SupabaseRepository();
    }

    // Synchronous method to fetch items
    public void loadItems(DataCallback<List<Item>> callback) {
        repository.fetchItems(new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }


    // Add other CRUD methods as needed
    public void addItem(Item newItem, DataCallback<Void> callback) {
        repository.addItem(newItem, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(null); // Notify the caller of success
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e); // Pass error to the callback
            }
        });
    }

    public void loadDatabases(String organizationName, DataCallback<List<DatabaseSelection>> callback) {
        repository.fetchDatabases(organizationName, new DataCallback<List<DatabaseSelection>>() {
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
}
