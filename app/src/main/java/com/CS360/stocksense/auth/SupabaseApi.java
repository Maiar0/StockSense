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

public interface SupabaseApi {
    @POST("auth/v1/token?grant_type=password")
    @Headers({"Content-Type: application/json"})
    Call<SecureLoginResponse> login(@Header("apikey") String apiKey, @Body SecureLoginRequest request);

    @POST("auth/v1/token?grant_type=refresh_token")
    @Headers({"Content-Type: application/json"})
    Call<SecureLoginResponse> refresh(@Header("apikey") String apiKey, @Body SecureLoginRequest request);

    @POST("auth/v1/signup")
    @Headers({"Content-Type: application/json"})
    Call<SecureLoginResponse> register(@Header("apikey") String apiKey, @Body SecureLoginRequest request);

    @GET("rest/v1/items")
    @Headers({"Content-Type: application/json"})
    Call<List<Item>> getOrganizationItems(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Query("organization_id") String organizationId
    );
    @PATCH("rest/v1/items")
    @Headers({"Content-Type: application/json"})
    Call<Void> updateItem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String accessToken,
            @Query("database_id") String databaseId,
            @Query("item_id") String itemId,
            @Body Item updatedItem
    );
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
