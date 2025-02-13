package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.CS360.stocksense.RecyclerAdapters.RecyclerSearchViewAdapter;
import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SearchViewActivity
 *
 * This activity provides a search interface for users to browse and manage items
 * within a selected database in the StockSense application. It displays the search
 * results in a list using a RecyclerView and allows users to interact with individual items.
 *
 * Inherits:
 * - Navigation functionality and shared UI components from `MainActivity`.
 *
 * Features:
 * - Displays a list of items fetched from a selected database in a vertical RecyclerView.
 * - Allows users to search for specific items using a search box.
 * - Provides navigation to item details when an item is selected.
 * - Includes options for deleting an item by ID or exporting the database to a CSV file.
 *
 * Key Methods:
 * - `initializeData()`: Fetches items from the selected database and populates the RecyclerView.
 * - `populateRecyclerView(List<Item>)`: Updates the RecyclerView with the fetched items.
 * - `onItemSelected(Item)`: Handles item selection and navigates to the item details screen.
 * - `handleNavigationButtonClickLeft()`: Navigates to the GridViewActivity.
 * - `handleNavigationButtonClickCenter()`: Prompts the user to delete an item by ID.
 * - `handleNavigationButtonClickRight()`: Exports the database to a CSV file.
 *
 * Example Usage:
 * - Invoked when a user selects a database and wishes to search and interact with its items.
 *
 * Notes:
 * - Leverages `DataManager` to fetch data from the backend.
 * - RecyclerView is configured with a custom adapter (`RecyclerSearchViewAdapter`).
 * - Requires `databaseId` to be passed as an intent extra for proper functionality.
 *
 * Dependencies:
 * - `DataManager` for backend data interactions.
 * - `RecyclerSearchViewAdapter` for displaying search results.
 * - Android's `SharedPreferences` for storing session data.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SearchViewActivity extends MainActivity {
    private RecyclerView recyclerView;
    private RecyclerSearchViewAdapter adapter;
    private EditText searchBox;
    private String databaseId;
    private Map<String, Item> itemIdMap = new HashMap<>();
    private Map<String, Item> itemNameMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        initializeNavigationBar(getString(R.string.nav_button1_search), getString(R.string.nav_button2_search), getString(R.string.nav_button3_search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize RecyclerView and EditText
        recyclerView = findViewById(R.id.recycler_search_results);
        searchBox = findViewById(R.id.search_box);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Searches on handler, so we do not search unnecessarily.(300 ms)
                new Handler().postDelayed(() -> filterItems(s.toString().trim()), 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        databaseId = getIntent().getStringExtra("selected_database");
        Log.d("SearchViewActivity", "Organization " + loggedInOrganization);

        initializeData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Handle back navigate to Database Selection
        Intent intent = new Intent(this, DbSelectionViewActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void handleNavigationButtonClickLeft() {
        // Handle navigate to grid view
        Intent intent = new Intent(this, GridViewActivity.class);
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    @Override
    protected void handleNavigationButtonClickCenter() {
        // Navigate to delete item work flow
        showInputDialog(
                "Delete Item",
                "Enter Item ID",
                "Delete",
                input -> deleteItemById(input, databaseId) // Pass the deleteDatabaseById method as the action
        );
    }
    @Override
    protected void handleNavigationButtonClickRight() {
        // Navigate to export work flow
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
                    initializeHashMaps();
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
     * Initializes HashMaps for fast lookups of items by ID and name.
     *
     * This method populates two HashMaps:
     * - `itemIdMap`: Maps item IDs to `Item` objects for quick retrieval.
     * - `itemNameMap`: Maps lowercase item names to `Item` objects for case-insensitive name-based searches.
     *
     * This improves search performance by avoiding repeated list iterations.
     */
    private void initializeHashMaps(){
        for (Item item : fetchedItems) {
            itemIdMap.put(item.getItemId(), item);
            itemNameMap.put(item.getItemName().toLowerCase(), item);
        }
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
    /**
     * Handles item selection from the RecyclerView.
     *
     * Displays a toast message with the selected item's name and navigates the user
     * to `ItemDetailsActivity` for more information.
     *
     * @param item The selected `Item` object.
     */
    private void onItemSelected(Item item) {
        Toast.makeText(this, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("selected_item", item.getItemId());
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    /**
     * Filters items based on the user's search query.
     *
     * - If the query matches an item ID exactly, it is retrieved from `itemIdMap`.
     * - If the query partially matches an item name, matching items are retrieved from `itemNameMap`.
     * - If the query is empty, the full list of fetched items is displayed.
     *
     * @param query The user's search input.
     */
    private void filterItems(String query) {
        if (query.isEmpty()) {
            populateRecyclerView(fetchedItems);
            return;
        }

        List<Item> filteredList = new ArrayList<>();

        if (itemIdMap.containsKey(query)) {
            filteredList.add(itemIdMap.get(query)); // Exact match on ID
        } else {
            for (String key : itemNameMap.keySet()) {
                if (key.contains(query.toLowerCase())) { // Partial match on name
                    filteredList.add(itemNameMap.get(key));
                }
            }
        }

        populateRecyclerView(filteredList);
    }

}