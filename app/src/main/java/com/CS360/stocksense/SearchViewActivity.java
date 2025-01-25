package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.Utils.CSVUtils;
import com.CS360.stocksense.models.Item;

import java.io.IOException;
import java.util.List;

public class SearchViewActivity extends MainActivity {
    private RecyclerView recyclerView;
    private RecyclerSearchViewAdapter adapter;
    private EditText searchBox;
    private String databaseId;
    private List<Item> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        initNav(getString(R.string.nav_button1_search), getString(R.string.nav_button2_search), getString(R.string.nav_button3_search));

        // Initialize RecyclerView and EditText
        recyclerView = findViewById(R.id.recycler_search_results);
        searchBox = findViewById(R.id.search_box);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO:: Set up RecyclerView

        databaseId = getIntent().getStringExtra("selected_database");
        Log.d("OnInstantiate", "SearchView " + organizationName + ' ' + databaseId);

        initData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, DbSelectionViewActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void onNavButton1Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 1 Clicked");
        Intent intent = new Intent(this, GridViewActivity.class);
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    @Override
    protected void onNavButton2Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 2 Clicked");
        showDeleteItemDialog();
    }
    @Override
    protected void onNavButton3Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 3 Clicked");
        exportDatabaseToCSV();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(() -> {
            //TODO:: // Update items in the database
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData(); // Reload data when resuming activity
    }


    @Override
    public void initData() {
        DataManager dataManager = new DataManager();

        dataManager.fetchDatabase(organizationName, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> fetchedItems) {
                runOnUiThread(() -> {
                    items = fetchedItems;
                    adapter = new RecyclerSearchViewAdapter(items, item -> onSelected(item));
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchViewActivity.this));
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(SearchViewActivity.this, "Error loading database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SearchView", "Error: " + e.getMessage(), e);
                });
            }
        });
    }
    private void showDeleteItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Database");

        // Add an input field for the database name
        final EditText input = new EditText(this);
        input.setHint("Enter Item ID");
        builder.setView(input);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String itemId = input.getText().toString().trim();
            if (!itemId.isEmpty()) {
                deleteItemById(itemId);
            } else {
                Toast.makeText(this, "itemID cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteItemById(String itemId) {
        DataManager dataManager = new DataManager();

        dataManager.deleteItem(organizationName, itemId, databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(SearchViewActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    initData(); // Refresh the list
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(SearchViewActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SearchViewActivity", "Error deleting item", e);
                });
            }
        });
    }

    private void exportDatabaseToCSV() {
        if (items == null || items.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // Define the file path in the Downloads directory
        String fileName = "database_export.csv";
        String filePath = getExternalFilesDir(null) + "/" + fileName;

        try {
            // Use CSVUtils to write data to the file
            CSVUtils.exportToCSV(filePath, items);

            Toast.makeText(this, "Database exported to: " + filePath, Toast.LENGTH_LONG).show();
            Log.d("ExportDatabase", "Exported to " + filePath);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to export database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ExportDatabase", "Error exporting database", e);
        }
    }


    private void onSelected(Item item) {
        Toast.makeText(this, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SearchViewActivity.this, ItemDetailsActivity.class);
        intent.putExtra("selected_item", item.getItem_id());
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }

}
