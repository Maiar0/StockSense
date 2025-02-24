package com.CS360.stocksense.auth;
/**
 * DataCallback Interface
 * <p>
 * A generic callback interface for handling asynchronous operations in Supabase data retrieval.
 * This interface allows handling both success and error cases for API calls or database queries.
 *
 * @param <T> The type of result expected on a successful operation.
 * <p>
 * Usage Example:
 * ```java
 * DataCallback<List<Item>> callback = new DataCallback<>() {
 *     @Override
 *     public void onSuccess(List<Item> result) {
 *         // Handle successful data retrieval
 *     }
 *
 *     @Override
 *     public void onError(Exception e) {
 *         // Handle error case
 *     }
 * };
 * ```
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 02/12/2025
 */
public interface DataCallback<T> {
    /**
     * Called when an operation completes successfully.
     *
     * @param result The resulting data of type T.
     */
    void onSuccess(T result);
    /**
     * Called when an error occurs during an operation.
     *
     * @param e The exception that occurred.
     */
    void onError(Exception e);
}
