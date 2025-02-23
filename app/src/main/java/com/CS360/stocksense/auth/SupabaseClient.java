package com.CS360.stocksense.auth;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides a singleton instance of the Retrofit client configured for the Supabase REST API.
 *
 * This class ensures that there is a single instance of Retrofit throughout the application,
 * configured with the base URL and the necessary converters for JSON serialization and deserialization.
 *
 * Purpose:
 * - To centralize the configuration of Retrofit.
 * - To provide an easy and consistent way to access the Supabase API endpoints.
 *
 * Example Usage:
 * ```java
 * Retrofit client = SupabaseClient.getInstance();
 * SupabaseApi api = client.create(SupabaseApi.class);
 * ```
 *
 * Notes:
 * - The base URL is hardcoded but can be modified to fit different environments (e.g., staging or production).
 * - Uses Gson for JSON parsing.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 02/02/2025
 */
public class SupabaseClient {

    // Base URL for the Supabase REST API
    private static final String BASE_URL = "https://hvqrsutsvovnizmipksd.supabase.co";

    // Singleton instance of Retrofit
    private static Retrofit retrofit;

    /**
     * Returns the singleton instance of the Retrofit client.
     * If the client has not been created yet, it initializes the instance with the base URL and Gson converter.
     *
     * @return The Retrofit instance configured for the Supabase API.
     */
    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Set the base URL for API requests
                    .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON serialization/deserialization
                    .build();
        }
        return retrofit;
    }
}
