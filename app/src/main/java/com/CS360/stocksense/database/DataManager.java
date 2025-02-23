package com.CS360.stocksense.database;

import static com.CS360.stocksense.Utils.Utils.generateDatabaseId;

import android.content.Context;
import android.util.Log;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private final Context appContext;
    private static DataUpdateListener updateListener;
    private final SupabaseRepository repository;
    private long lastFetch = -1;
    private List<Item> toUpdate;
    private Map<String, List<Item>> itemsByDatabase = new HashMap<>();

    private DataManager(Context context){
        this.appContext = context.getApplicationContext();
        toUpdate = new ArrayList<>();
        repository = new SupabaseRepository(context);
        Log.d(this.getClass().getSimpleName(), "Initializing DataManager with organizationId: " + repository.getOrganization());
    }

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
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemId().equals(itemId)) {
                itemList.set(i, updatedItem); // Replace the existing item
                itemFound = true;
                Log.d(this.getClass().getSimpleName(), "Updated local item: " + updatedItem.getItemName());
                notifyDataUpdated();
            }
        }
        if(!itemFound){
            Log.e(this.getClass().getSimpleName(), "Item not found in local list itemdId: " + itemId);
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
    public void createNewDatabase(String databaseName){
        // Create an Item for the Database to hold
        Item newDatabaseItem = new Item();
        newDatabaseItem.setDatabaseName(databaseName);
        newDatabaseItem.setOrganizationId(repository.getOrganization());
        newDatabaseItem.setDatabaseId(generateDatabaseId());
        insertItem(newDatabaseItem);
        notifyDataUpdated();
    }
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
    public void updateOrganization(String joinOrganization) {

        repository.updateOrganization(joinOrganization, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(this.getClass().getSimpleName(), "Successfully updated organization for item: " + joinOrganization);
            }

            @Override
            public void onError(Exception e) {
                Log.e(this.getClass().getSimpleName(), "Failed to update organization: " + e.getMessage());
            }
        });
    }
     public void createOrganization(){
        // TODO:: Implement creating organization
     }


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

    // Process the list of items into a Map grouped by DatabaseId
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
    public List<Item> sortItemsByName(List<Item> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return itemList; // Return as is if the list is null or empty
        }
        Collections.sort(itemList, Comparator.comparing(Item::getItemName, String.CASE_INSENSITIVE_ORDER));
        return itemList; // Sorted list
    }
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

    public List<Item> getItemsByDatabaseId(String databaseId) {
        if (itemsByDatabase.containsKey(databaseId)) {
            return itemsByDatabase.get(databaseId); // Returns the list of items for the given databaseId
        }
        return new ArrayList<>(); // Returns an empty list if the databaseId is not found
    }

    public Item getItemById(String databaseId, String itemId) {
        if (itemsByDatabase.containsKey(databaseId)) {
            List<Item> itemList = itemsByDatabase.get(databaseId);
            for (Item item : itemList) {
                if (item.getItemId().equals(itemId)) {
                    return item; // Found the item, return it
                }
            }
        }
        return null; // Return null if not found
    }

    // Clear cached data when needed
    public void clearCache() {
        itemsByDatabase.clear();
    }
    public interface DataUpdateListener {
        void onDataUpdated();
    }
    public static void setUpdateListener(DataUpdateListener listener) {
        updateListener = listener;
    }

    private void notifyDataUpdated() {
        if (updateListener != null) {
            updateListener.onDataUpdated();
        }else{
            Log.d(this.getClass().getSimpleName(), "Listener for updates is null");
        }
    }



}
