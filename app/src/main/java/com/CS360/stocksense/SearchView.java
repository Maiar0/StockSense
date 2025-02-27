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
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SearchView provides a search interface for users to browse and manage items
 * within a selected database in the StockSense application.
 *
 * <p>
 * Features:
 * - Displays a list of items from a selected database in a RecyclerView.
 * - Allows users to search for items using a search box.
 * - Navigates to item details when an item is selected.
 * - Supports deleting an item by ID or exporting the database to a CSV file.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SearchView extends MainActivity {
    private RecyclerView recyclerView;
    private RecyclerSearchViewAdapter adapter;
    private String databaseId;
    private Map<String, Item> itemIdMap = new HashMap<>();
    private Map<String, Item> itemNameMap = new HashMap<>();
    /**
     * Initializes the activity, search functionality, and RecyclerView.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        initializeNavigationBar(getString(R.string.nav_button1_search), getString(R.string.nav_button2_search), getString(R.string.nav_button3_search));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize RecyclerView and EditText
        recyclerView = findViewById(R.id.recycler_search_results);
        EditText searchBox = findViewById(R.id.search_box);

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

        initializeData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Handle back navigate to Database Selection
        Intent intent = new Intent(this, DbSelectionView.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void handleNavigationButtonClickLeft() {
        // Handle navigate to grid view
        Intent intent = new Intent(this, GridView.class);
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
                input -> deleteItemById(databaseId,input) // Pass the deleteDatabaseById method as the action
        );
    }
    @Override
    protected void handleNavigationButtonClickRight() {
        // Navigate to export work flow
        exportDatabaseToCSV();
    }
    /**
     * Fetches and displays the list of items from the selected database.
     */
    @Override
    public void initializeData() {
        List<Item> items = DataManager.getInstance(SearchView.this).getItemsByDatabaseId(databaseId);
        if(items.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "InitData: Fetched: " + items.size() + " Adjusting to fetchedItems: " + fetchedItems.size());
            return;
        }
        fetchedItems = items;
        initializeHashMaps();
        Log.d(this.getClass().getSimpleName(), "InitData: Fetched " + items.size() + " Items.");
        populateRecyclerView(items);
    }
    /**
     * Initializes HashMaps for fast lookups of items by ID and name.
     * <p>
     * This method populates two HashMaps:
     * - `itemIdMap`: Maps item IDs to `Item` objects for quick retrieval.
     * - `itemNameMap`: Maps lowercase item names to `Item` objects for case-insensitive name-based searches.
     * <p>
     * This improves search performance by avoiding repeated list iterations.
     */
    private void initializeHashMaps(){
        if(fetchedItems.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "InitHashMaps: provided list is empty fetchedItems: " + fetchedItems.size() );
            return;
        }
        for (Item item : fetchedItems) {
            itemIdMap.put(item.getItemId(), item);
            itemNameMap.put(item.getItemName().toLowerCase(), item);
        }
    }

    /**
     * Updates the RecyclerView with the given items list.
     *
     * @param items List of items to display.
     */
    private void populateRecyclerView(List<Item> items) {
        if(items.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "PopulateRecyclerView: provided list is empty items: " + items.size() );
            return;
        }
        if(adapter != null){
            Log.d(this.getClass().getSimpleName(), "PopulateRecyclerView: Refreshing Data: " + items.size() );
            adapter.updateData(items);
        }else{
            Log.d(this.getClass().getSimpleName(), "PopulateRecyclerView: Creating recycler: " + items.size() );
            adapter = new RecyclerSearchViewAdapter(items, this::onItemSelected);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }
    /**
     * Handles item selection and navigates to the item details screen.
     *
     * @param item The selected item.
     */
    private void onItemSelected(Item item) {
        Toast.makeText(this, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ItemDetailsView.class);
        intent.putExtra("selected_item", item.getItemId());
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    /**
     * Filters items based on the user's search query.
     * <p>
     * - If the query matches an item ID exactly, it is retrieved from `itemIdMap`.
     * - If the query partially matches an item name, matching items are retrieved from `itemNameMap`.
     * - If the query is empty, the full list of fetched items is displayed.
     *
     * @param query The user's search input.
     */
    private void filterItems(String query) {
        Log.d(this.getClass().getSimpleName(), "filterItems ");
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
        if(filteredList.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "filterItems: filteredList is empty: " + filteredList.size());
        }else{
            populateRecyclerView(filteredList);
        }
    }
}