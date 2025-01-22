package com.CS360.stocksense.Supabase;

public interface DataCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
