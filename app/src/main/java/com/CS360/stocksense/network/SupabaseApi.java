package com.CS360.stocksense.network;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.DatabaseSelection;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SupabaseApi {
    @GET("items")
    Call<List<Item>> getItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken
    );

    @POST("items")
    Call<Void> addItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body Item newItem
    );

    @GET("items")
    Call<List<Item>> getAllItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken
    );

    @GET("items")
    Call<List<DatabaseSelection>> fetchOrganization(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationFilter,
            @Query("select") String select
    );

    @GET("items")
    Call<List<Item>> fetchItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationFilter,
            @Query("database_id") String databaseFilter
    );

    @GET("items")
    Call<List<Item>> fetchItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationFilter,
            @Query("item_id") String itemFilter
    );
    @POST("items")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"   // Request the created record in the response
    })
    Call<List<Item>> createDatabase(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body Map<String, String> newDatabase
    );
    @DELETE("items")
    Call<Void> deleteDatabase(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("database_id") String databaseId
    );
    @POST("items")
    Call<Void> insertItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Body List<Item> items
    );
    //---------------------------------------------------------------------------------------------//
    // Create Items
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

    // Read Item
    @GET("items")
    Call<List<Item>> readItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationName,
            @Query("item_id") String itemId,
            @Query("database_id") String databaseId
    );

    // Update Item
    @PUT("items")
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation" // Request to return updated records
    })
    Call<List<Item>> updateItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationName,
            @Query("item_id") String itemId,
            @Query("database_id") String databaseId,
            @Body Item item
    );

    // Delete Item
    @DELETE("items")
    Call<Void> deleteItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationName,
            @Query("item_id") String itemId,
            @Query("database_id") String databaseId
    );

}
