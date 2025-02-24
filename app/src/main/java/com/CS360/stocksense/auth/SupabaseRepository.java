package com.CS360.stocksense.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.SecureLoginRequest;
import com.CS360.stocksense.models.SecureLoginResponse;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * SupabaseRepository handles authentication and database interactions with the Supabase backend.
 *
 * <p>
 * Features:
 * - Manages authentication, including login, registration, and token refresh.
 * - Provides methods for CRUD operations on inventory items.
 * - Handles user organization management, including joining and creating organizations.
 * - Stores authentication tokens securely using SharedPreferences.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SupabaseRepository {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh2cXJzdXRzdm92bml6bWlwa3NkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzc0MjMwNjAsImV4cCI6MjA1Mjk5OTA2MH0.Dr0fvzfUNaH3AdhGhsOP11kFW5t4KFL999Oetog0wWY"; // TODO:: Move to secure storage later
    private static final String PREFS_NAME = "com.CS360.stocksense.PREFERENCES_FILE";// TODO:: May want to move this somewhere else
    private static final String TOKEN_KEY = "AccessToken";
    private static final String REFRESH_KEY = "RefreshToken";
    private static final String ORGANIZATION_KEY = "OrganizationId";
    private final SupabaseApi authApi;
    private final SharedPreferences prefs;
    /**
     * Initializes the repository with the given context.
     *
     * @param context The application context.
     */
    public SupabaseRepository(Context context) {
        authApi = SupabaseClient.getInstance().create(SupabaseApi.class);
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    /**
     * Authenticates a user using email and password.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param callback The callback for handling authentication success or failure.
     */
    public void loginUser(String email, String password, DataCallback<String> callback) {
        SecureLoginRequest request = new SecureLoginRequest(email, password);

        authApi.login(API_KEY, request).enqueue(new Callback<SecureLoginResponse>() {
            @Override
            public void onResponse(Call<SecureLoginResponse> call, Response<SecureLoginResponse> response) {
                Log.d("SupabaseRepository", "Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Raw Response: " + response.raw());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SupabaseRepository", "Success to Login" + response.body());
                    SecureLoginResponse loginResponse = response.body();
                    Log.d("SupabaseRepository", "MetaData: " + loginResponse.getUserMetadata());
                    saveTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken(), loginResponse.getUserMetadata().get("organization_id").toString());// Saves Data to Prefs
                    // Extract organization_id from user metadata
                    callback.onSuccess(loginResponse.getAccessToken());
                } else {
                    Log.d("SupabaseRepository", "Failed to Login");
                    callback.onError(new Exception("Login failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<SecureLoginResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    /**
     * Registers a new user account.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param callback The callback for handling success or failure.
     */
    public void registerUser(String email, String password, DataCallback<String> callback) {
        SecureLoginRequest request = new SecureLoginRequest(email, password);

        authApi.register(API_KEY, request).enqueue(new Callback<SecureLoginResponse>() {
            @Override
            public void onResponse(Call<SecureLoginResponse> call, Response<SecureLoginResponse> response) {
                Log.d("SupabaseRepository", "Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Raw Response: " + response.raw());
                if (response.isSuccessful() && response.body() != null) {
                    SecureLoginResponse loginResponse = response.body();
                    Log.d("SupabaseRepository", "MetaData: " + loginResponse.getUserMetadata());
                    saveTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken(), loginResponse.getUserMetadata().get("organization_id").toString());// Saves Data to Prefs
                    callback.onSuccess("User registered successfully.");
                } else {
                    callback.onError(new Exception("Registration failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<SecureLoginResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    /**
     * Refreshes an authentication token using a stored refresh token.
     *
     * @param callback The callback for handling success or failure.
     */
    public void refreshToken(DataCallback<String> callback) {
        String refreshToken = prefs.getString(REFRESH_KEY, null);
        if (refreshToken == null) {
            callback.onError(new Exception("No refresh token stored."));
            return;
        }

        SecureLoginRequest request = new SecureLoginRequest(refreshToken);
        authApi.refresh(API_KEY, request).enqueue(new Callback<SecureLoginResponse>() {
            @Override
            public void onResponse(Call<SecureLoginResponse> call, Response<SecureLoginResponse> response) {
                Log.d("SupabaseRepository", "Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Raw Response: " + response.raw());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SupabaseRepository", "Raw Response: " + response.raw());
                    SecureLoginResponse loginResponse = response.body();
                    saveTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken(), loginResponse.getUserMetadata().get("organization_id").toString());// Saves Data to Prefs
                    callback.onSuccess(response.body().getAccessToken());
                } else {
                    callback.onError(new Exception("Token refresh failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<SecureLoginResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    /**
     * Saves authentication tokens and organization ID to SharedPreferences.
     *
     * @param accessToken    The access token.
     * @param refreshToken   The refresh token.
     * @param organizationId The organization ID.
     */
    private void saveTokens(String accessToken, String refreshToken, String organizationId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, accessToken);
        editor.putString(REFRESH_KEY, refreshToken);
        editor.putString(ORGANIZATION_KEY, organizationId);
        editor.apply();
    }
    /**
     * Retrieves the stored access token.
     *
     * @return The access token, or null if not found.
     */
    public String getAccessToken() {
        return prefs.getString(TOKEN_KEY, null);
    }
    /**
     * Retrieves the stored organization ID.
     *
     * @return The organization ID, or null if not found.
     */
    public String getOrganization(){return prefs.getString(ORGANIZATION_KEY, null);}
    /**
     * Logs out the user by clearing stored authentication data.
     */
    public void logout() {// Removes Authentication Data from Device
        prefs.edit().clear().apply();
    }

    /**
     * Fetches all items associated with the current organization.
     *
     * @param callback The callback for handling the retrieved items or errors.
     */
    public void getOrganizationItems( DataCallback<List<Item>> callback) {
        String accessToken = getAccessToken();
        //Log.d("SupabaseRepository", "Access Token: " + accessToken);
        if (accessToken == null) {
            callback.onError(new Exception("No access token available."));
            return;
        }
        String organizationId = getOrganization();
        authApi.getOrganizationItems(API_KEY, "Bearer " + accessToken, "eq." + organizationId).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("SupabaseRepository", "Get Items Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Get items Raw Response: " + response.raw());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Failed to retrieve items: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    /**
     * Updates an item in the database.
     *
     * @param databaseId  The database ID.
     * @param itemId      The ID of the item to update.
     * @param updatedItem The updated item object.
     * @param callback    The callback for handling success or failure.
     */
    public void updateItem(String databaseId, String itemId, Item updatedItem, DataCallback<Void> callback) {
        String authToken = "Bearer " + getAccessToken();

        /* Debugging logs to verify request
        Log.d("SupabaseRepository", "PATCH URL: rest/v1/items?database_id=eq." + databaseId + "&item_id=eq." + itemId);
        Log.d("SupabaseRepository", "PATCH Body: " + new Gson().toJson(updatedItem));*/

        authApi.updateItem(API_KEY, authToken, "eq." + databaseId, "eq." + itemId, updatedItem)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("SupabaseRepository", "Get Items Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                        Log.d("SupabaseRepository", "Get items Raw Response: " + response.raw());
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onError(new Exception("Failed to retrieve items: " + response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Supabase", "Network error while updating item: " + t.getMessage());
                        callback.onError(new Exception("Network error: " + t.getMessage()));
                    }
                });
    }
    /**
     * Inserts a new item into the database.
     *
     * @param insertItem The item to insert.
     * @param callback   Callback to handle success or failure.
     */
    public void insertItem(Item insertItem, DataCallback<List<Item>> callback){
        String authToken = "Bearer " + getAccessToken();

        authApi.insertItem(API_KEY, authToken, insertItem)
                .enqueue(new Callback<List<Item>>() {
                             @Override
                             public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                                 Log.d("SupabaseRepository", "Insert Item Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                                 Log.d("SupabaseRepository", "Insert item Raw Response: " + response.raw());
                                 if (response.isSuccessful()) {
                                     callback.onSuccess(response.body());
                                 } else {
                                     callback.onError(new Exception("Failed to insert items: " + response.message()));
                                 }
                             }

                             @Override
                             public void onFailure(Call<List<Item>> call, Throwable t) {
                                 Log.e("SupabaseRepository", "Network error while inserting item: " + t.getMessage());
                                 callback.onError(new Exception("Network error: " + t.getMessage()));
                             }
                         }

                );
    }
    /**
     * Deletes an item from the database.
     *
     * @param databaseId The database ID where the item is stored.
     * @param itemId     The ID of the item to delete.
     * @param callback   Callback to handle success or failure.
     */
    public void deleteItem(String databaseId, String itemId, DataCallback<List<Item>> callback){
        String authToken = "Bearer " + getAccessToken();
        if(itemId  != null ){
            itemId = "eq." + itemId;
        }
        authApi.deleteItem(API_KEY, authToken, "eq."+ databaseId, itemId)
                .enqueue(new Callback<List<Item>>() {
                    @Override
                    public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                        Log.d("SupabaseRepository", "Delete Item Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                        Log.d("SupabaseRepository", "Delete item Raw Response: " + response.raw());
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onError(new Exception("Failed to delete items: " + response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Item>> call, Throwable t) {
                        Log.e("SupabaseRepository", "Network error while deleting item: " + t.getMessage());
                        callback.onError(new Exception("Network error: " + t.getMessage()));
                    }
                });
    }
    /**
     * Updates the quantity of an item in the database.
     *
     * @param databaseId    The database ID.
     * @param itemId        The ID of the item.
     * @param quantityChange The change in quantity.
     * @param callback      Callback to handle success or failure.
     */
    public void updateItemQuantity(String databaseId, String itemId, int quantityChange, DataCallback<Void> callback){
        String authToken = "Bearer " + getAccessToken();
        Map<String, Object> body = new HashMap<>();
        body.put("p_database_id", databaseId);
        body.put("p_item_id", itemId);
        body.put("p_quantity_change", quantityChange);

        authApi.updateItemQuantity(API_KEY, authToken, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("SupabaseRepository", "Change Quantity Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Change Quantity Raw Response: " + response.raw());
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Failed to Change Quantity: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error while Changing Quantity: " + t.getMessage());
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    /**
     * Updates the organization ID for a user.
     *
     * @param joinOrganization The new organization ID.
     * @param callback         Callback to handle success or failure.
     */
    public void updateOrganization(String joinOrganization, DataCallback<Void> callback){
        String authToken = "Bearer " + getAccessToken();
        JsonObject body = new JsonObject();
        body.addProperty("new_org_id", joinOrganization);

        authApi.updateOrganization(API_KEY, authToken, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("SupabaseRepository", "Change Organization Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Change Organization Raw Response: " + response.raw());
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Failed to Change Organization: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error while Changing Organization: " + t.getMessage());
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
    /**
     * Creates a new organization in the database.
     *
     * @param organizationName The name of the new organization.
     * @param callback         Callback to handle success or failure.
     */
    public void createOrganization(String organizationName, DataCallback<String> callback){
        String authToken = "Bearer " + getAccessToken();
        Map<String, Object> body = new HashMap<>();
        body.put("org_name", organizationName);
        authApi.createOrganization(API_KEY, authToken, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("SupabaseRepository", "Create Organization Response Code: " + response.code());// TODO:: Remove logging of sensitive data
                Log.d("SupabaseRepository", "Create Organization Raw Response: " + response.raw());
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Failed to Create Organization: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("SupabaseRepository", "Network error while Create Organization: " + t.getMessage());
                callback.onError(new Exception("Network error: " + t.getMessage()));
            }
        });
    }
}
