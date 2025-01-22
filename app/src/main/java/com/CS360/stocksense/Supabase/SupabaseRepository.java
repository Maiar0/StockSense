package com.CS360.stocksense.Supabase;

import android.util.Log;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.network.SupabaseApi;
import com.CS360.stocksense.network.SupabaseClient;

import java.util.List;

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

    public void fetchDatabases(String organizationName, DataCallback<List<DatabaseSelection>> callback) {
        Log.d("SupabaseRepository", "Fetching databases for organization: " + organizationName);

        api.getDatabasesFiltered(apiKey, authToken, "eq." + organizationName).enqueue(new Callback<List<DatabaseSelection>>() {
            @Override
            public void onResponse(Call<List<DatabaseSelection>> call, Response<List<DatabaseSelection>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SupabaseRepository", "Databases fetched successfully: " + response.body().toString());
                    callback.onSuccess(response.body());
                } else {
                    Log.e("SupabaseRepository", "Error fetching databases: " + response.message());
                    callback.onError(new Exception("Error fetching databases: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<DatabaseSelection>> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error occurred: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }



    public List<Item> fetchItemsSync() throws Exception {
        // Execute the API call synchronously
        Response<List<Item>> response = api.getAllItems(apiKey, authToken).execute();

        if (response.isSuccessful() && response.body() != null) {
            return response.body(); // Return the list of items
        } else {
            throw new Exception("Error fetching items: " + response.message());
        }
    }

    // Add a new item
    public void addItem(Item newItem, DataCallback<Void> callback) {
        api.addItem(apiKey, authToken, newItem).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Error adding item: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
}
