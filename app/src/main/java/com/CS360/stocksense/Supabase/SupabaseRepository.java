package com.CS360.stocksense.Supabase;

import android.util.Log;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.LoginRequest;
import com.CS360.stocksense.models.Organization;
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
/**
 * SupabaseRepository
 *
 * This class serves as a data access layer for interacting with the Supabase API.
 * It handles operations such as fetching, creating, updating, and deleting records
 * for organizations, databases, and items.
 *
 * Responsibilities:
 * - Communicates with Supabase API endpoints.
 * - Implements asynchronous network calls using Retrofit.
 * - Processes and returns data via callbacks.
 *
 * Security Note:
 * - API keys should be stored securely in environment variables or configuration files.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SupabaseRepository {
    private final SupabaseApi api;
    private final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh2cXJzdXRzdm92bml6bWlwa3NkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzc0MjMwNjAsImV4cCI6MjA1Mjk5OTA2MH0.Dr0fvzfUNaH3AdhGhsOP11kFW5t4KFL999Oetog0wWY"; // Replace with your actual API key
    private final String authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh2cXJzdXRzdm92bml6bWlwa3NkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzc0MjMwNjAsImV4cCI6MjA1Mjk5OTA2MH0.Dr0fvzfUNaH3AdhGhsOP11kFW5t4KFL999Oetog0wWY"; // Replace with your actual API token

    /**
     * Initializes the repository and connects to Supabase API.
     */
    public SupabaseRepository() {
        api = SupabaseClient.getInstance().create(SupabaseApi.class);
    }

    /**
     * Fetches the list of databases for an organization.
     *
     * @param organizationName The name of the organization.
     * @param callback Callback to handle the response.
     */
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

    /**
     * Fetches all items from a specific database.
     *
     * @param organizationName The name of the organization.
     * @param databaseId The ID of the database.
     * @param callback Callback to handle the response.
     */
    public void fetchDatabase(String organizationName, String databaseId, DataCallback<List<Item>> callback) {
        Log.d("SupabaseRepository", "Fetching items for organization: " + organizationName + ", database: " + databaseId);

        api.fetchDatabase(
                apiKey,
                authToken,
                "eq." + organizationName,
                "eq." + databaseId
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

    /**
     * Deletes a database from Supabase.
     *
     * @param databaseId The ID of the database to delete.
     * @param callback Callback to handle the response.
     */
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

    /**
     * Creates multiple items in a database.
     *
     * @param organizationName The name of the organization.
     * @param items The list of items to create.
     * @param databaseId The ID of the target database.
     * @param callback Callback to handle the response.
     */
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

    /**
     * Reads a single item from the database.
     *
     * @param organizationName The name of the organization.
     * @param itemId The ID of the item to retrieve.
     * @param databaseId The ID of the database containing the item.
     * @param callback Callback to handle the response.
     */
    public void readItem(String organizationName, String itemId, String databaseId, DataCallback<Item> callback) {
        api.fetchItem(apiKey, authToken, "eq." + organizationName, "eq." + itemId, "eq." + databaseId).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("SupabaseRepository", "Raw Response: " + response.raw());
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
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

    /**
     * Updates an existing item in the database.
     *
     * @param organizationName The name of the organization.
     * @param item The item to update.
     * @param databaseId The ID of the database containing the item.
     * @param callback Callback to handle the response.
     */
    public void updateItem(String organizationName, Item item, String databaseId, DataCallback<List<Item>> callback) {
        api.updateItem(apiKey, authToken, "eq." + organizationName, "eq." + item.getItemId(), "eq." + databaseId, item).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("SupabaseRepository", "Raw Response: " + response.raw());
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

    /**
     * Deletes a specific item from the database.
     *
     * @param organizationName The name of the organization.
     * @param itemId The ID of the item to delete.
     * @param databaseId The ID of the database containing the item.
     * @param callback Callback to handle the response.
     */
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
    /**
     * Registers a new user in Supabase.
     *
     * @param organization The user object containing username, password, and organizations.
     * @param callback The callback for success or failure.
     */
    public void registerUser(Organization organization, DataCallback<Organization> callback) {
        api.registerUser(apiKey, authToken, organization).enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
                Log.d("SupabaseRepository", "Response Code: " + response.code());
                Log.d("SupabaseRepository", "Raw Response: " + response.raw());

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Organization createdOrganization = response.body().get(0);
                    Log.d("SupabaseRepository", "Created Organization: " + new Gson().toJson(createdOrganization));
                    callback.onSuccess(createdOrganization);
                } else {
                    callback.onError(new Exception("Registration failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Organization>> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error: " + t.getMessage());
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }

    /**
     * Validates user credentials with Supabase.
     *
     * @param username The username to authenticate.
     * @param hashedPassword The hashed password for authentication.
     * @param callback Callback to handle authentication success or failure.
     */
    public void validateUser(String username, String hashedPassword, DataCallback<Boolean> callback) {
        LoginRequest loginRequest = new LoginRequest(username, hashedPassword);
        Log.d("LoginRequest", "LoginRequest: " + loginRequest.toString());
        Log.d("LoginRequest", "Sending payload: " + new Gson().toJson(loginRequest));

        api.validateUser(apiKey, authToken, loginRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError(new Exception("Invalid username or password."));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }


}
