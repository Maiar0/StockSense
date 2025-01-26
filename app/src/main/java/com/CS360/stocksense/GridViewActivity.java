package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GridViewActivity extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerGridViewAdapter adapter;
    private String databaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        initializeNavigationBar(getString(R.string.nav_button1_grid), getString(R.string.nav_button2_grid), getString(R.string.nav_button3_search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_grid_view);

        Log.d("GridViewActivity", "Organization " + loggedInOrganization );

        databaseId = getIntent().getStringExtra("selected_database");
        initializeData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, DbSelectionViewActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void handleNavigationButtonClickLeft(){
        Intent intent = new Intent(this, SearchViewActivity.class);
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    @Override
    protected void handleNavigationButtonClickCenter(){
        //TODO:: Implement Item creation
    }
    @Override
    protected void handleNavigationButtonClickRight(){
        exportDatabaseToCSV();
    }
    @Override
    protected void initializeData() {
        DataManager dataManager = new DataManager();

        dataManager.fetchDatabase(loggedInOrganization, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                runOnUiThread(() -> {
                    fetchedItems = items;
                    Log.d("GridViewActivity", "Fetched " + fetchedItems.size() + " items.");
                    populateRecyclerView(fetchedItems);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Error loading database: " + e.getMessage());
                    Log.e("GridViewActivity", "Error: " + e.getMessage(), e);
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
        adapter = new RecyclerGridViewAdapter(items, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns
        recyclerView.setAdapter(adapter);
    }
    private void onItemSelected(Item item) {
        Toast.makeText(this, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("selected_item", item.getItem_id());
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    //TODO:: Implement Sort Algorithmic
    private List<Item> sortData(List<Item> itemsList) {
        Collections.sort(itemsList, Comparator.comparing(Item::getItemName)); // Sort items by name
        return itemsList;
    }
}
