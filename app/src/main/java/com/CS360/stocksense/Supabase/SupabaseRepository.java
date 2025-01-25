package com.CS360.stocksense.Supabase;

import android.util.Log;

import com.CS360.stocksense.Utils.Utils;
import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.network.SupabaseApi;
import com.CS360.stocksense.network.SupabaseClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupabaseRepository {
    private final SupabaseApi api;
    private final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh2cXJzdXRzdm92bml6bWlwa3NkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzc0MjMwNjAsImV4cCI6MjA1Mjk5OTA2MH0.Dr0fvzfUNaH3AdhGhsOP11kFW5t4KFL999Oetog0wWY"; // Replace with your actual API key
    private final String authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh2cXJzdXRzdm92bml6bWlwa3NkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzc0MjMwNjAsImV4cCI6MjA1Mjk5OTA2MH0.Dr0fvzfUNaH3AdhGhsOP11kFW5t4KFL999Oetog0wWY"; // Replace with your actual API token

    public SupabaseRepository() {
        api = SupabaseClient.getInstance().create(SupabaseApi.class);
    }

    // Fetch all items
    public void fetchItems(DataCallback<List<Item>> callback) {
        Log.d("SupabaseRepository", "Starting fetchItems()...");

        api.getItems(apiKey, authToken).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("SupabaseRepository", "Finalized URL: " + call.request().url());
                if (response.isSuccessful() && response.body() != null) {
                    // Log successful fetch
                    Log.d("SupabaseRepository", "Data fetched successfully: " + response.body().toString());

                    callback.onSuccess(response.body());
                } else {
                    // Log server error
                    Log.e("SupabaseRepository", "Error fetching items: " + response.message());
                    Log.e("SupabaseRepository", "Response Code: " + response.code());
                    callback.onError(new Exception("Error fetching items: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                // Log network failure
                Log.e("SupabaseRepository", "Network error occurred: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });

        Log.d("SupabaseRepository", "fetchItems() request sent.");
    }

    public void fetchOrganization(String organizationName, DataCallback<List<DatabaseSelection>> callback) {
        api.fetchOrganization(apiKey, authToken, "eq." + organizationName, "database_id,database_name").enqueue(new Callback<List<DatabaseSelection>>() {
            @Override
            public void onResponse(Call<List<DatabaseSelection>> call, Response<List<DatabaseSelection>> response) {
                Log.d("SupabaseRepository", "Finalized URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    List<DatabaseSelection> fetchedDatabases = response.body();

                    // Post-process the data to ensure unique database_id
                    Map<String, DatabaseSelection> uniqueDatabases = new HashMap<>();
                    for (DatabaseSelection db : fetchedDatabases) {
                        uniqueDatabases.put(db.getId(), db);
                    }

                    // Return the processed unique list
                    callback.onSuccess(new ArrayList<>(uniqueDatabases.values()));
                } else {
                    Log.e("SupabaseRepository", "Error fetching databases: " + response.message());
                    callback.onError(new Exception("Error fetching databases: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<DatabaseSelection>> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void fetchDatabase(String organizationName, String databaseId, DataCallback<List<Item>> callback) {
        Log.d("SupabaseRepository", "Fetching items for organization: " + organizationName + ", database: " + databaseId);

        api.fetchItems(
                apiKey,
                authToken,
                "eq." + organizationName, // Apply filter for organization_name
                "eq." + databaseId        // Apply filter for database_id
        ).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("SupabaseRepository", "Finalized URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SupabaseRepository", "Items fetched successfully: " + response.body().toString());
                    callback.onSuccess(response.body());
                } else {
                    Log.e("SupabaseRepository", "Error fetching items: " + response.message());
                    callback.onError(new Exception("Error fetching items: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }
    public void fetchSingleItem(String organizationName, int itemId, DataCallback<Item> callback) {
        Log.d("SupabaseRepository", "Fetching item for Organization: " + organizationName + ", ItemID: " + itemId);

        api.fetchItem(
                apiKey,
                authToken,
                "eq." + organizationName, // Filter for organization
                "eq." + itemId            // Filter for item ID
        ).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                // Log the finalized URL
                Log.d("SupabaseRepository", "Finalized URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("SupabaseRepository", "Item fetched successfully: " + response.body().get(0).toString());
                    callback.onSuccess(response.body().get(0)); // Return the first item
                } else {
                    Log.e("SupabaseRepository", "Error fetching item: " + response.message());
                    callback.onError(new Exception("Error fetching item: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void createNewDatabase(String databaseName, String organizationName, DataCallback<Item> callback) {
        Map<String, String> newDatabase = new HashMap<>();
        newDatabase.put("database_name", databaseName);
        newDatabase.put("organization_name", organizationName);
        newDatabase.put("database_id", Utils.generateDatabaseId());

        Log.d("SupabaseRepository", "Payload: " + newDatabase.toString());

        api.createDatabase(apiKey, authToken, newDatabase).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("SupabaseRepository", "Finalized URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("SupabaseRepository", "Database created successfully: " + response.body().get(0).toString());
                    callback.onSuccess(response.body().get(0));
                } else {
                    Log.e("SupabaseRepository", "Error creating database: " + response.message());
                    Log.e("SupabaseRepository", "Response code: " + response.code());
                    Log.e("SupabaseRepository", "Error body: " + response.errorBody().toString());
                    callback.onError(new Exception("Error creating database: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }
    public void deleteDatabase(String databaseId, DataCallback<Void> callback) {
        api.deleteDatabase(apiKey, authToken, "eq." + databaseId).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Error deleting database: " + response.message()));
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    public void insertItems(List<Item> items, DataCallback<Void> callback) {
        api.insertItems(apiKey, authToken, items).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Error inserting items: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
//--------------------------------------------------------------------------------------------------------------------//
// Create Items
public void createItem(String organizationName, List<Item> items, String databaseId, DataCallback<List<Item>> callback) {
    // Attach organization_name and database_id to each item
    for (Item item : items) {
        item.setOrganizationName(organizationName);
        item.setDatabaseId(databaseId);
    }

    Gson gson = new Gson();
    String payloadJson = gson.toJson(items);
    Log.d("SupabaseRepository", "Payload: " + payloadJson);
    api.createItems(apiKey, authToken, items).enqueue(new Callback<List<Item>>() {
        @Override
        public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else {
                callback.onError(new Exception("Error creating items: " + response.message()));
            }
        }

        @Override
        public void onFailure(Call<List<Item>> call, Throwable t) {
            callback.onError(new Exception("Network error: " + t.getMessage()));
        }
    });
}

    // Read Single Item
    public void readItem(String organizationName, String itemId, String databaseId, DataCallback<Item> callback) {
        api.readItem(apiKey, authToken, "eq." + organizationName, itemId, "eq." + databaseId).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0)); // Return the first matched item
                } else {
                    callback.onError(new Exception("Error reading item: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }

    // Update Item
    public void updateItem(String organizationName, Item item, String databaseId, DataCallback<List<Item>> callback) {
        api.updateItem(apiKey, authToken, "eq." + organizationName, item.getItem_id(), "eq." + databaseId, item).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error updating item: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }

    // Delete Item
    public void deleteItem(String organizationName, String itemId, String databaseId, DataCallback<Void> callback) {
        api.deleteItem(apiKey, authToken, "eq." + organizationName, "eq."+itemId, "eq." + databaseId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("SupabaseRepository", "Finalized URL: " + call.request().url());
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Error deleting item: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });

    }












}
