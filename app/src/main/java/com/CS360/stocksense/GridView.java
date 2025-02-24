package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.RecyclerAdapters.RecyclerGridViewAdapter;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.List;
import java.util.Objects;

/**
 * GridViewActivity
 * <p>
 * This activity provides a grid-based view to display items fetched from a specific database in the StockSense application.
 * It extends `MainActivity` to leverage shared navigation and functionality.
 * <p>
 * Features:
 * - Displays a grid layout of items using a RecyclerView.
 * - Fetches data from a database and populates the RecyclerView.
 * - Includes navigation buttons for transitioning between activities and exporting data to CSV.
 * <p>
 * Responsibilities:
 * - Fetches and displays items specific to the selected database.
 * - Provides navigation to other activities (e.g., SearchViewActivity, DbSelectionViewActivity).
 * - Handles user interactions such as item selection and export operations.
 * <p>
 * Example Usage:
 * - Invoked when the user selects a database to view its items in a grid format.
 * <p>
 * Notes:
 * - Uses `DataManager` to fetch data from the backend.
 * - RecyclerView layout is configured as a 2-column grid.
 * - Pending functionalities include item creation and advanced sorting.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class GridView extends MainView {

    private RecyclerView recyclerView;
    private String databaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        initializeNavigationBar(getString(R.string.nav_button1_grid), getString(R.string.nav_button2_grid), getString(R.string.nav_button3_search));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_grid_view);

        Log.d("GridViewActivity", "Organization " + organizationId);

        databaseId = getIntent().getStringExtra("selected_database");
        initializeData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Handle back navigation to Database Selection
        Intent intent = new Intent(this, DbSelectionView.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void handleNavigationButtonClickLeft(){
        // Navigate to SearchView
        Intent intent = new Intent(this, SearchView.class);
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    @Override
    protected void handleNavigationButtonClickCenter(){
        // Create item work flow
        showCreateItemDialog();
    }
    @Override
    protected void handleNavigationButtonClickRight(){
        //Export database workflow
        exportDatabaseToCSV();
    }
    @Override
    protected void initializeData() {
        List<Item> items = DataManager.getInstance(GridView.this).getItemsByDatabaseId(databaseId);
        fetchedItems = items;
        Log.d("GridViewActivity", "Fetched " + items.size() + " databases.");
        populateRecyclerView(items);
    }
    /**
     * Populates the RecyclerView with the given list of items.
     *
     * @param items List of Item objects to display.
     */
    private void populateRecyclerView(List<Item> items) {
        RecyclerGridViewAdapter adapter = new RecyclerGridViewAdapter(this, items, this::onItemSelected);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns
        recyclerView.setAdapter(adapter);
    }

    /**
     * Displays a dialog to create a new item with input fields for item details.
     * The user enters values for item name, ID, quantity, location, and alert level.
     * After validation, the item is created and saved in the database.
     */
    private void showCreateItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Item");

        // Layout for input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Set input fields
        EditText itemName_input = new EditText(this);
        itemName_input.setHint("Enter Item Name");
        itemName_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(itemName_input);

        EditText ItemId_input = new EditText(this);
        ItemId_input.setHint("Enter item Id");
        ItemId_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(ItemId_input);

        EditText quantity_input = new EditText(this);
        quantity_input.setHint("Enter Quantity");
        quantity_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantity_input);

        EditText location_input = new EditText(this);
        location_input.setHint("Enter Location");
        location_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(location_input);

        EditText alertLevel_input = new EditText(this);
        alertLevel_input.setHint("Enter Alert level");
        alertLevel_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(alertLevel_input);

        builder.setView(layout);

        // Set dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String itemName = itemName_input.getText().toString().trim();
            String itemId = ItemId_input.getText().toString().trim();
            String quantity = quantity_input.getText().toString().trim();//this a number
            String location = location_input.getText().toString().trim();
            String alertLevel = alertLevel_input.getText().toString().trim();

            if (itemName.isEmpty() || itemId.isEmpty() || quantity.isEmpty() || location.isEmpty() || alertLevel.isEmpty()) {
                showToast("All fields are required.");
                return;
            }

            Item item = new Item();
            item.setItemId(itemId);
            item.setItemName(itemName);
            item.setQuantity(Integer.parseInt(quantity));
            item.setLocation(location);
            item.setAlertLevel(Integer.parseInt(alertLevel));
            item.setOrganizationId(organizationId);
            item.setDatabaseId(databaseId);
            DataManager.getInstance(this).insertItem(item);//Creates Item
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    /**
     * Handles item selection from the RecyclerView.
     * <p>
     * Displays a toast message with the selected item's name and navigates the user
     * to `ItemDetailsActivity` for more information.
     *
     * @param item The selected `Item` object.
     */
    private void onItemSelected(Item item) {
        Toast.makeText(this, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ItemDetailsView.class);
        intent.putExtra("selected_item", item.getItemId());
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
}
