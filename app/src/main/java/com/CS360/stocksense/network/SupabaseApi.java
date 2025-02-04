package com.CS360.stocksense.network;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.LoginRequest;
import com.CS360.stocksense.models.Organization;
import com.CS360.stocksense.models.DatabaseSelection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Interface for interacting with the Supabase REST API.
 * Provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on items, databases, and organizations within the Supabase backend.
 *
 * Each method corresponds to an endpoint and uses Retrofit annotations
 * to specify HTTP methods, headers, and query parameters.
 */
public interface SupabaseApi {

    /**
     * Creates multiple items in the database.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param items A list of items to be created in the database.
     * @return A Call object with a list of created items returned by the server.
     */
    @POST("items")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation" // Request to return created records
    })
    Call<List<Item>> createItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body List<Item> items
    );

    /**
     * Fetches a single item from the database based on specified filters.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param organizationName The organization name to filter the results.
     * @param itemId The unique ID of the item to retrieve.
     * @param databaseId The database ID where the item resides.
     * @return A Call object containing a list of matching items.
     */
    @GET("items")
    Call<List<Item>> fetchItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationName,
            @Query("item_id") String itemId,
            @Query("database_id") String databaseId
    );

    /**
     * Fetches all items from a specific database.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param organizationFilter The organization name to filter the results.
     * @param databaseFilter The database ID to filter the results.
     * @return A Call object containing a list of items in the specified database.
     */
    @GET("items")
    Call<List<Item>> fetchDatabase(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationFilter,
            @Query("database_id") String databaseFilter
    );

    /**
     * Fetches metadata about an organization.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param organizationFilter The organization name to filter the results.
     * @param select The columns to include in the results.
     * @return A Call object containing a list of database selections for the organization.
     */
    @GET("items")
    Call<List<DatabaseSelection>> fetchOrganization(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationFilter,
            @Query("select") String select
    );

    /**
     * Updates a specific item in the database.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param organizationName The organization name to filter the update.
     * @param itemId The unique ID of the item to update.
     * @param databaseId The database ID where the item resides.
     * @param item The updated item object.
     * @return A Call object with a list of updated items returned by the server.
     */
    @PATCH("items")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    Call<List<Item>> updateItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationName,
            @Query("item_id") String itemId,
            @Query("database_id") String databaseId,
            @Body Item item
    );

    /**
     * Deletes a specific item from the database.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param organizationName The organization name to filter the deletion.
     * @param itemId The unique ID of the item to delete.
     * @param databaseId The database ID where the item resides.
     * @return A Call object indicating success or failure of the deletion.
     */
    @DELETE("items")
    Call<Void> deleteItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationName,
            @Query("item_id") String itemId,
            @Query("database_id") String databaseId
    );

    /**
     * Deletes all items in a specific database.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param databaseId The ID of the database to delete items from.
     * @return A Call object indicating success or failure of the deletion.
     */
    @DELETE("items")
    Call<Void> deleteDatabase(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("database_id") String databaseId
    );
    /**
     * Registers a new user in Supabase.
     *
     * @param apiKey The API key for authentication.
     * @param authToken The authorization token for user authentication.
     * @param organization The user object containing username, password, and organizations.
     * @return A Call object for handling the API response.
     */
    @POST("users")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation" // Ensures we get the created organization
    })
    Call<List<Organization>> registerUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body Organization organization
    );


    @POST("rpc/validate_user")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    Call<Void> validateUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body LoginRequest loginRequest
    );

}
