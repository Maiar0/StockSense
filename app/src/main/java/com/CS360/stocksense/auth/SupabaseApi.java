package com.CS360.stocksense.auth;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.SecureLoginRequest;
import com.CS360.stocksense.models.SecureLoginResponse;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * SupabaseApi defines the REST API interface for communicating with Supabase services.
 *
 * <p>
 * Features:
 * - Supports authentication via login, refresh tokens, and user registration.
 * - Enables CRUD operations for managing items in the database.
 * - Provides remote procedure calls (RPC) for updating item quantity and managing organizations.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public interface SupabaseApi {

    /**
     * Authenticates a user using an email and password.
     *
     * @param apiKey  The API key for authentication.
     * @param request The login request containing user credentials.
     * @return A {@link Call} object containing a {@link SecureLoginResponse}.
     */
    @POST("auth/v1/token?grant_type=password")
    @Headers({"Content-Type: application/json"})
    Call<SecureLoginResponse> login(
            @Header("apikey") String apiKey,
            @Body SecureLoginRequest request
    );

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param apiKey  The API key for authentication.
     * @param request The request containing the refresh token.
     * @return A {@link Call} object containing a {@link SecureLoginResponse}.
     */
    @POST("auth/v1/token?grant_type=refresh_token")
    @Headers({"Content-Type: application/json"})
    Call<SecureLoginResponse> refresh(
            @Header("apikey") String apiKey,
            @Body SecureLoginRequest request
    );

    /**
     * Registers a new user account.
     *
     * @param apiKey  The API key for authentication.
     * @param request The request containing user registration details.
     * @return A {@link Call} object containing a {@link SecureLoginResponse}.
     */
    @POST("auth/v1/signup")
    @Headers({"Content-Type: application/json"})
    Call<SecureLoginResponse> register(
            @Header("apikey") String apiKey,
            @Body SecureLoginRequest request
    );

    /**
     * Retrieves all items belonging to a specific organization.
     *
     * @param apiKey         The API key for authentication.
     * @param accessToken    The user's access token.
     * @param organizationId The organization ID to filter items.
     * @return A {@link Call} object containing a list of {@link Item} objects.
     */
    @GET("rest/v1/items")
    @Headers({"Content-Type: application/json"})
    Call<List<Item>> getOrganizationItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Query("organization_id") String organizationId
    );

    /**
     * Updates an existing item in the database.
     *
     * @param apiKey      The API key for authentication.
     * @param accessToken The user's access token.
     * @param databaseId  The database ID where the item is stored.
     * @param itemId      The ID of the item to update.
     * @param updatedItem The updated item details.
     * @return A {@link Call} object for handling the update response.
     */
    @PATCH("rest/v1/items")
    @Headers({"Content-Type: application/json"})
    Call<Void> updateItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Query("database_id") String databaseId,
            @Query("item_id") String itemId,
            @Body Item updatedItem
    );

    /**
     * Inserts a new item into the database.
     *
     * @param apiKey      The API key for authentication.
     * @param accessToken The user's access token.
     * @param insertItem  The item to insert.
     * @return A {@link Call} object containing the inserted {@link Item} objects.
     */
    @POST("rest/v1/items")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<List<Item>> insertItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Body Item insertItem
    );

    /**
     * Deletes an item from the database.
     *
     * @param apiKey      The API key for authentication.
     * @param accessToken The user's access token.
     * @param databaseId  The database ID where the item is stored.
     * @param itemId      The ID of the item to delete.
     * @return A {@link Call} object containing the deleted {@link Item} objects.
     */
    @DELETE("rest/v1/items")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<List<Item>> deleteItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Query("database_id") String databaseId,
            @Query("item_id") String itemId
    );

    /**
     * Updates an item's quantity in the database via an RPC call.
     *
     * @param apiKey      The API key for authentication.
     * @param accessToken The user's access token.
     * @param body        The request body containing update parameters.
     * @return A {@link Call} object for handling the update response.
     */
    @POST("rest/v1/rpc/update_item_quantity")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<Void> updateItemQuantity(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Body Map<String, Object> body
    );

    /**
     * Updates an organization's ID in the database via an RPC call.
     *
     * @param apiKey      The API key for authentication.
     * @param accessToken The user's access token.
     * @param body        The request body containing the new organization ID.
     * @return A {@link Call} object for handling the update response.
     */
    @POST("rest/v1/rpc/update_organization_id")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<Void> updateOrganization(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Body JsonObject body
    );

    /**
     * Creates a new organization in the database via an RPC call.
     *
     * @param apiKey      The API key for authentication.
     * @param accessToken The user's access token.
     * @param body        The request body containing organization details.
     * @return A {@link Call} object containing the new organization's ID.
     */
    @POST("rest/v1/rpc/create_organization")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<String> createOrganization(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Body Map<String, Object> body
    );
}
