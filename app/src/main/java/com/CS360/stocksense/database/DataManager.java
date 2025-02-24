package com.CS360.stocksense.database;

import static com.CS360.stocksense.Utils.Utils.generateDatabaseId;

import android.content.Context;
import android.util.Log;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * DataManager is responsible for managing data retrieval, caching, and updates within the StockSense application.
 *
 * <p>
 * Features:
 * - Singleton instance to ensure centralized data management.
 * - Fetches and updates organization items using a repository.
 * - Stores data locally in memory for quick access.
 * - Supports CRUD operations for databases and items.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class DataManager {
    private static DataManager instance;
    private static DataUpdateListener updateListener;
    private final SupabaseRepository repository;
    private long lastFetch = -1;
    private Map<String, List<Item>> itemsByDatabase = new HashMap<>();
    /**
     * Private constructor to initialize the repository and log the organization ID.
     *
     * @param context The application context.
     */
    private DataManager(Context context){
        repository = new SupabaseRepository(context);
        Log.d(this.getClass().getSimpleName(), "Initializing DataManager with organizationId: " + repository.getOrganization());
    }
    /**
     * Retrieves the singleton instance of DataManager, ensuring only one instance exists.
     *
     * @param context The application context.
     * @return The singleton instance of DataManager.
     */
    public static DataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager(context);
                }
            }
        }
        return instance;
    }
    /**
     * Fetches organization items and updates the local cache.
     *
     * @param callback Callback to handle success or error responses.
     */
    public void fetchOrganizationItems(DataCallback<List<Item>> callback) {
        Log.d(this.getClass().getSimpleName(), "Fetching organization items for organizationId: " + repository.getOrganization());

        repository.getOrganizationItems( new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items ) {
                Log.d(this.getClass().getSimpleName(), "Successfully fetched " + items.size() + " items.");
                processItemsByDatabase(items);
                lastFetch = System.currentTimeMillis();
                Log.d(this.getClass().getSimpleName(), "fetch timestamp set: " + lastFetch);
                callback.onSuccess(items);
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Error fetching organization items: " + e.getMessage());
                callback.onError(e);
            }
        });
    }
    /**
     * Updates an existing item in the local cache and repository.
     *
     * @param updatedItem The item to be updated.
     */
    public void updateItem(Item updatedItem) {
        if (updatedItem == null) return;

        String databaseId = updatedItem.getDatabaseId();
        String itemId = updatedItem.getItemId();

        if (!itemsByDatabase.containsKey(databaseId)) {
            Log.e(this.getClass().getSimpleName(), "Database ID not found Locally : " + databaseId);
            checkLastFetch();
            return;
        }

        List<Item> itemList = itemsByDatabase.get(databaseId);
        boolean itemFound = false;
        if(itemList == null){return;}
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemId().equals(itemId)) {
                itemList.set(i, updatedItem); // Replace the existing item
                itemFound = true;
                Log.d(this.getClass().getSimpleName(), "Updated local item: " + updatedItem.getItemName());
                notifyDataUpdated();
            }
        }
        if(!itemFound){
            Log.e(this.getClass().getSimpleName(), "Item not found in local list itemId: " + itemId);
            return;//TODO:: Should we return, add item, or fetch repository again?
        }

        repository.updateItem(databaseId, itemId, updatedItem, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(this.getClass().getSimpleName(), "Successfully updated updatedItem: " + itemId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Update failed for updatedItem " + itemId + ": " + e.getMessage());
            }
        });
        checkLastFetch();
    }
    /**
     * Inserts a new item into the repository and updates the local cache.
     *
     * @param insertItem The item to be inserted.
     */
    public void insertItem(Item insertItem){
        repository.insertItem(insertItem, new DataCallback<List<Item>>(){
            @Override
            public void onSuccess(List<Item> result) {
                for (Item item : result) {
                    String databaseId = item.getDatabaseId();
                    itemsByDatabase.computeIfAbsent(databaseId, k -> new ArrayList<>()).add(item);
                }
                notifyDataUpdated();
                Log.d(this.getClass().getSimpleName(), "Successfully inserted insertItem: " + insertItem.getDatabaseId());
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Update failed for insertItem " + insertItem.getDatabaseId() + ": " + e.getMessage());
            }
        });
        checkLastFetch();
    }
    /**
     * Creates a new database entry with a generated ID.
     *
     * @param databaseName The name of the new database.
     */
    public void createNewDatabase(String databaseName){
        // Create an Item for the Database to hold
        Item newDatabaseItem = new Item();
        newDatabaseItem.setDatabaseName(databaseName);
        newDatabaseItem.setOrganizationId(repository.getOrganization());
        newDatabaseItem.setDatabaseId(generateDatabaseId());
        insertItem(newDatabaseItem);
        notifyDataUpdated();
    }
    /**
     * Imports a new database with a given name and a list of items.
     *
     * @param databaseName The name of the imported database.
     * @param items        The list of items to be associated with this database.
     */
    public void importNewDatabase(String databaseName, List<Item> items){
        String databaseId = generateDatabaseId();
        for(Item item : items){
            item.setDatabaseName(databaseName);
            item.setOrganizationId(repository.getOrganization());
            item.setDatabaseId(databaseId);
            insertItem(item);
            itemsByDatabase.computeIfAbsent(databaseId, k -> new ArrayList<>()).add(item);
        }
        notifyDataUpdated();
    }
    /**
     * Deletes an item from the database and updates the local cache.
     *
     * @param databaseId The database ID where the item is stored.
     * @param itemId     The ID of the item to be deleted.
     */
    public void deleteItem(String databaseId, String itemId){
        repository.deleteItem(databaseId, itemId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                Log.d(this.getClass().getSimpleName(), "Successfully deleted itemId: " + itemId + ": databaseId: " + databaseId);
                deleteItemLocal(databaseId, itemId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Delete failed for itemId " + itemId + ": databaseId: " + databaseId + " message: "  + e.getMessage());
            }
        });
    }
    /**
     * Removes an item from the local cache. If the item ID is null, removes the entire database entry.
     *
     * @param databaseId The database ID where the item is stored.
     * @param itemId     The ID of the item to be removed. If null, deletes the entire database entry.
     */
    public void deleteItemLocal(String databaseId, String itemId){
        List<Item> items = itemsByDatabase.get(databaseId);
        if( items == null){
            Log.d(this.getClass().getSimpleName(), "items list for local deletion null" );
        }
        if(itemId == null){
            Log.d(this.getClass().getSimpleName(), "deleteItemLocal: Deleted database: " + databaseId + " Reason: itemId null");
            itemsByDatabase.remove(databaseId);
            notifyDataUpdated();
            return;
        }
        boolean itemFound = false;
        if(items == null){return;}
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemId().equals(itemId)) {
                Log.d(this.getClass().getSimpleName(), "Removed item Locally itemId: " + itemId + " Reason: ItemId was parsed and found");
                items.remove(i);
                itemFound = true;
                break;// Don't decrement break
            }
        }
        if (!itemFound) {
            Log.d(this.getClass().getSimpleName(), "Item with ID " + itemId + " not found in local storage");
        }
        Log.d(this.getClass().getSimpleName(), "hashMap: " + itemsByDatabase);
        notifyDataUpdated();
    }
    /**
     * Updates the quantity of an item in the database and reflects the change in local storage.
     *
     * @param databaseId     The database ID where the item is stored.
     * @param itemId         The ID of the item to be updated.
     * @param quantityChange The amount to adjust the item's quantity by.
     */
    public void updateItemQuantity(String databaseId, String itemId, int quantityChange) {
        if (databaseId == null || itemId == null) {
            Log.e(this.getClass().getSimpleName(), "Invalid parameters: databaseId or itemId is null");
            return;
        }
        repository.updateItemQuantity(databaseId, itemId, quantityChange, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(this.getClass().getSimpleName(), "Successfully updated quantity for item: " + itemId);

                // Update locally
                List<Item> items = itemsByDatabase.get(databaseId);
                if (items != null) {
                    for (Item item : items) {
                        if (item.getItemId().equals(itemId)) {
                            item.setQuantity(item.getQuantity() + quantityChange);
                            Log.d(this.getClass().getSimpleName(), "Updated local item: " + itemId + ", New Quantity: " + item.getQuantity());
                            break;
                        }
                    }
                }
                // Notify UI of data change
                notifyDataUpdated();
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Failed to update item quantity: " + e.getMessage());
            }
        });
    }
    /**
     * Updates the organization ID for a user.
     *
     * @param joinOrganization The new organization ID to assign to the user.
     */
    public void updateOrganization(String joinOrganization) {

        repository.updateOrganization(joinOrganization, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(this.getClass().getSimpleName(), "Successfully updated organization for item: " + joinOrganization);
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Failed to updated organization: " + e.getMessage());
            }
        });
    }
    /**
     * Creates a new organization in the database.
     *
     * @param databaseName The name of the new organization.
     * @param callback     Callback to handle success or failure of the organization creation.
     */
     public void createOrganization(String databaseName, DataCallback<String> callback){
        repository.createOrganization(databaseName, new DataCallback<String>(){
            @Override
            public void onSuccess(String result) {
                Log.d(this.getClass().getSimpleName(), "Successfully created organization: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Failed to create organization: " + e.getMessage());
                callback.onError(e);
            }
        });
     }

    /**
     * Checks the last fetch timestamp and updates organization items if more than 60 seconds have passed.
     * Ensures data remains up-to-date while minimizing redundant fetches.
     */
    private void checkLastFetch() {
        long currentTime = System.currentTimeMillis();

        // If last fetch is not set (-1) or it's been more than 60 seconds
        if (lastFetch == -1 || (currentTime - lastFetch) > 60000) {
            Log.d(this.getClass().getSimpleName(), "More than 60 seconds since last fetch. Updating data...");

            // Then fetch new organization items
            fetchOrganizationItems(new DataCallback<List<Item>>() {
                @Override
                public void onSuccess(List<Item> result) {
                    Log.d(this.getClass().getSimpleName(), "Organization items updated successfully.");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(this.getClass().getSimpleName(), "Error fetching organization items: " + e.getMessage());
                }
            });

            // Update last fetch timestamp
            lastFetch = currentTime;
        } else {
            Log.d(this.getClass().getSimpleName(), "Less than 60 seconds since last fetch, skipping update.");
        }
    }

    /**
     * Organizes and groups a list of items by their respective database IDs.
     * Clears the existing map and updates it with sorted items to maintain efficient retrieval.
     *
     * @param items The list of items to be grouped and processed.
     */
    private void processItemsByDatabase(List<Item> items) {
        Log.d(this.getClass().getSimpleName(), "Processing " + items.size() + " items into database groups.");
        items = sortItemsByName(items);
        itemsByDatabase.clear();
        for (Item item : items) {
            String databaseId = item.getDatabaseId(); // Ensure this method exists in `Item`
            itemsByDatabase.computeIfAbsent(databaseId, k -> new ArrayList<>()).add(item);
        }
        // Log the number of items per database
        for (Map.Entry<String, List<Item>> entry : itemsByDatabase.entrySet()) {
            Log.d(this.getClass().getSimpleName(), "Database ID: " + entry.getKey() + " | Item Count: " + entry.getValue().size());
        }
        notifyDataUpdated();
    }
    /**
     * Sorts a list of items alphabetically by name.
     *
     * @param itemList The list of items to be sorted.
     * @return The sorted list of items.
     */
    public List<Item> sortItemsByName(List<Item> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return itemList; // Return as is if the list is null or empty
        }
        itemList.sort(Comparator.comparing(Item::getItemName, String.CASE_INSENSITIVE_ORDER));
        return itemList; // Sorted list
    }
    /**
     * Retrieves a list of database selections, mapping each database ID to its name.
     *
     * @return A list of {@link DatabaseSelection} objects representing available databases.
     */
    public List<DatabaseSelection> getDatabaseSelections() {
        List<DatabaseSelection> databaseSelections = new ArrayList<>();

        for (Map.Entry<String, List<Item>> entry : itemsByDatabase.entrySet()) {
            String databaseId = entry.getKey();
            List<Item> items = entry.getValue();

            if (!items.isEmpty()) {
                String databaseName = items.get(0).getDatabaseName();
                databaseSelections.add(new DatabaseSelection(databaseId, databaseName));
            }
        }

        return databaseSelections;
    }
    /**
     * Retrieves a list of items for a specific database ID.
     *
     * @param databaseId The database ID to fetch items for.
     * @return A list of items belonging to the specified database.
     */
    public List<Item> getItemsByDatabaseId(String databaseId) {
        if (itemsByDatabase.containsKey(databaseId)) {
            return itemsByDatabase.get(databaseId); // Returns the list of items for the given databaseId
        }
        return new ArrayList<>(); // Returns an empty list if the databaseId is not found
    }
    /**
     * Retrieves a specific item by database ID and item ID.
     *
     * @param databaseId The database ID where the item is stored.
     * @param itemId The unique identifier of the item.
     * @return The matching Item object, or null if not found.
     */
    public Item getItemById(String databaseId, String itemId) {
        if (itemsByDatabase.containsKey(databaseId)) {
            List<Item> itemList = itemsByDatabase.get(databaseId);
            if(itemList== null ){return null;}
            for (Item item : itemList) {
                if (item.getItemId().equals(itemId)) {
                    return item; // Found the item, return it
                }
            }
        }
        return null; // Return null if not found
    }

    /**
     * Clears the cached database items when needed.
     */
    public void clearCache() {
        itemsByDatabase.clear();
    }
    /**
     * Interface for handling data updates.
     */
    public interface DataUpdateListener {
        /**
         * Called when data has been updated.
         */
        void onDataUpdated();
    }
    /**
     * Sets the listener for data updates.
     *
     * @param listener The listener to receive data update events.
     */
    public static void setUpdateListener(DataUpdateListener listener) {
        updateListener = listener;
    }
    /**
     * Notifies the registered listener that data has been updated.
     * If no listener is set, logs a debug message.
     */
    private void notifyDataUpdated() {
        if (updateListener != null) {
            updateListener.onDataUpdated();
        }else{
            Log.d(this.getClass().getSimpleName(), "Listener for updates is null");
        }
    }
}
