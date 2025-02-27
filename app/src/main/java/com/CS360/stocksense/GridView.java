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
 * GridView provides a grid-based view to display items fetched from a selected database
 * in the StockSense application.
 *
 * <p>
 * Features:
 * - Displays a grid layout of items using a RecyclerView.
 * - Fetches data from a database and populates the RecyclerView.
 * - Includes navigation for transitioning between views and exporting data.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class GridView extends MainActivity {

    private RecyclerView recyclerView;
    private String databaseId;
    private RecyclerGridViewAdapter adapter;
    /**
     * Initializes the activity, navigation bar, and RecyclerView.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
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
    /**
     * Fetches and displays the list of items from the selected database.
     */
    @Override
    protected void initializeData() {
        List<Item> items = DataManager.getInstance(GridView.this).getItemsByDatabaseId(databaseId);
        fetchedItems = items;
        Log.d("GridViewActivity", "Fetched " + items.size() + " databases.");
        populateRecyclerView(items);
    }
    /**
     * Updates the RecyclerView with the given items list.
     *
     * @param items List of items to display.
     */
    private void populateRecyclerView(List<Item> items) {
        if(adapter != null){
            adapter.updateData(items);
        }else{
            RecyclerGridViewAdapter adapter = new RecyclerGridViewAdapter(this, items, this::onItemSelected);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(adapter);
        }

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
     * Handles item selection and navigates to item details.
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
}
