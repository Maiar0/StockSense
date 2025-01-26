package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.List;

public class SearchViewActivity extends MainActivity {
    private RecyclerView recyclerView;
    private RecyclerSearchViewAdapter adapter;
    private EditText searchBox;
    private String databaseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        initializeNavigationBar(getString(R.string.nav_button1_search), getString(R.string.nav_button2_search), getString(R.string.nav_button3_search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize RecyclerView and EditText
        recyclerView = findViewById(R.id.recycler_search_results);
        searchBox = findViewById(R.id.search_box);

        databaseId = getIntent().getStringExtra("selected_database");
        Log.d("SearchViewActivity", "Organization " + loggedInOrganization);

        initializeData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, DbSelectionViewActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    @Override
    protected void handleNavigationButtonClickLeft() {
        Intent intent = new Intent(this, GridViewActivity.class);
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }

    @Override
    protected void handleNavigationButtonClickCenter() {
        showInputDialog(
                "Delete Item",
                "Enter Item ID",
                "Delete",
                input -> deleteItemById(input, databaseId) // Pass the deleteDatabaseById method as the action
        );
    }

    @Override
    protected void handleNavigationButtonClickRight() {
        exportDatabaseToCSV();
    }

    @Override
    public void initializeData() {
        DataManager dataManager = new DataManager();

        dataManager.fetchDatabase(loggedInOrganization, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                runOnUiThread(() -> {
                    fetchedItems = items;
                    Log.d("SearchViewActivity", "Fetched " + fetchedItems.size() + " items.");
                    populateRecyclerView(fetchedItems);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Error loading database: " + e.getMessage());
                    Log.e("SearchViewActivity", "Error: " + e.getMessage(), e);
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
        adapter = new RecyclerSearchViewAdapter(items, this::onItemSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void onItemSelected(Item item) {
        Toast.makeText(this, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("selected_item", item.getItem_id());
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
}