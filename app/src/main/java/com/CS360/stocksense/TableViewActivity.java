package com.CS360.stocksense;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.RecyclerAdapters.RecyclerTableViewAdapter;
import com.CS360.stocksense.models.Item;

import java.util.List;
/**
 * TableViewActivity
 *
 * This activity was designed to provide a table-based view for displaying items
 * from a specific database in the StockSense application. It extends `MainActivity`
 * to leverage shared navigation and data management features.
 *
 * Current Status:
 * - This activity is currently unused and may be removed in future updates if it is
 *   deemed unnecessary for the application.
 *
 * Intended Features:
 * - Fetch data from a selected database using `DataManager` and display it in a RecyclerView.
 * - Support operations like item deletion and confirmation dialogs.
 *
 * Key Methods:
 * - `initializeData()`: Fetches items from the database and prepares them for display.
 * - `populateRecyclerView(List<Item>)`: Populates the RecyclerView with fetched items.
 * - `showDeleteConfirmationDialog(Item)`: Displays a confirmation dialog for deleting an item.
 *
 * Notes:
 * - The activity is configured to use `RecyclerTableViewAdapter` for displaying data.
 * - Navigation and shared functionality are inherited from `MainActivity`.
 * - Future consideration should be given to its removal if no further use cases are identified.
 *
 * Dependencies:
 * - `DataManager` for backend interactions.
 * - `RecyclerTableViewAdapter` for handling RecyclerView item rendering.
 * @deprecated
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
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
        Log.d("TableViewActivity", "Organization " + organizationId);
        initializeData();
    }



    /**
     * Fetches data from the database and populates the RecyclerView.
     */
    @Override
    public void initializeData() {
        // TODO:: Init data
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
