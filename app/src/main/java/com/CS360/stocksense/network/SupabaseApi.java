package com.CS360.stocksense.network;

import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.models.DatabaseSelection;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("databases")
    Call<List<DatabaseSelection>> getDatabasesFiltered(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("organization_name") String organizationFilter
    );

}
