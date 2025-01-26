package com.CS360.stocksense;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.RecyclerAdapters.RecyclerTableViewAdapter;
import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.List;

public class TableViewActivity extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerTableViewAdapter adapter;
    private String databaseId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);

        recyclerView = findViewById(R.id.recycler_table_view);

        initializeNavigationBar("o","o","o");

        databaseId = getIntent().getStringExtra("selected_database");
        Log.d("TableViewActivity", "Organization " + loggedInOrganization);
        initializeData();
    }



    /**
     * Fetches data from the database and populates the RecyclerView.
     */
    @Override
    public void initializeData() {
        DataManager dataManager = new DataManager();

        dataManager.fetchDatabase(loggedInOrganization, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                runOnUiThread(() -> {
                    fetchedItems = items;
                    Log.d("TableViewActivity", "Fetched " + fetchedItems.size() + " items.");
                    populateRecyclerView(fetchedItems);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Error loading database: " + e.getMessage());
                    Log.e("TableViewActivity", "Error: " + e.getMessage(), e);
                });
            }
        });
    }

    /**
     * Populates the RecyclerView with the given list of items.
     *
     * @param items List of Item objects to display.
     */
    private void populateRecyclerView(List<Item> items) {
        adapter = new RecyclerTableViewAdapter(items, this::showDeleteConfirmationDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void showDeleteConfirmationDialog(Item current) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem(current))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteItem(Item item) {
        //TODO Implement Method
    }
}
