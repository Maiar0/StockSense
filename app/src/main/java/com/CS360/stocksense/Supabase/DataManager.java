package com.CS360.stocksense.Supabase;

import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.List;
/**
 * DataManager
 *
 * This class serves as a central handler for data operations using the Supabase repository.
 * It provides methods to fetch, create, and delete database records while managing callbacks.
 *
 * Responsibilities:
 * - Fetching organization and database records.
 * - Creating and deleting items in the database.
 * - Utilizing asynchronous callbacks for handling API responses.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class DataManager {
    private final SupabaseRepository repository;
    private List<Item> items = new ArrayList<>();
    private List<DatabaseSelection> databases = new ArrayList<>();
    /**
     * Initializes the DataManager with a Supabase repository instance.
     */
    public DataManager() {
        repository = new SupabaseRepository();
    }

    /**
     * Fetches a list of databases for the given organization.
     *
     * @param organizationName The name of the organization.
     * @param callback Callback to handle the response.
     */
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

    /**
     * Fetches all items from a specific database.
     *
     * @param organizationName The name of the organization.
     * @param databaseId The ID of the database.
     * @param callback Callback to handle the response.
     */
    public void fetchDatabase(String organizationName, String databaseId, DataCallback<List<Item>> callback) {
        repository.fetchDatabase(organizationName, databaseId, new DataCallback<List<Item>>() {
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

    /**
     * Deletes a database from Supabase.
     *
     * @param databaseId The ID of the database to delete.
     * @param callback Callback to handle the response.
     */
    public void deleteDatabase(String databaseId, DataCallback<Void> callback) {
        SupabaseRepository repository = new SupabaseRepository();
        repository.deleteDatabase(databaseId, callback);
    }

    /**
     * Fetches a single item from the database.
     *
     * @param organizationName The name of the organization.
     * @param itemId The ID of the item.
     * @param databaseId The ID of the database containing the item.
     * @param callback Callback to handle the response.
     */
    public void fetchSingleItem(String organizationName, String itemId, String databaseId, DataCallback<Item> callback) {
        repository.readItem(organizationName, itemId, databaseId, new DataCallback<Item>() {
            @Override
            public void onSuccess(Item result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Creates multiple items in a database.
     *
     * @param organizationName The name of the organization.
     * @param items The list of items to create.
     * @param databaseId The ID of the target database.
     * @param callback Callback to handle the response.
     */
    // Create items
    public void createItems(String organizationName, List<Item> items, String databaseId, DataCallback<List<Item>> callback) {
        repository.createItem(organizationName, items, databaseId, new DataCallback<List<Item>>() {
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

    /**
     * Deletes a specific item from a database.
     *
     * @param organizationName The name of the organization.
     * @param itemId The ID of the item to delete.
     * @param databaseId The ID of the database containing the item.
     * @param callback Callback to handle the response.
     */
    public void deleteItem(String organizationName, String itemId, String databaseId, DataCallback<Void> callback) {
        repository.deleteItem(organizationName, itemId, databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
