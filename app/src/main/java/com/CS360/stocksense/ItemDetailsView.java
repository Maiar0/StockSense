package com.CS360.stocksense;

import static com.CS360.stocksense.MainView.PREFERENCES_FILE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.Objects;

/**
 * ItemDetailsView provides a detailed view of a selected item, allowing users to edit or delete it.
 *
 * <p>
 * Features:
 * - Displays item details retrieved from the database.
 * - Allows users to edit and save item modifications.
 * - Provides a delete option with a confirmation dialog.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */

public class ItemDetailsView extends AppCompatActivity {

    private TextView itemHeader, itemIdInput;
    private EditText itemQuantity, itemLocation, itemAlertLevel, itemName;
    private String itemId;
    private String organizationId;
    private String databaseId;
    private Item currentItem;
    /**
     * Initializes the activity and loads item details.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemHeader = findViewById(R.id.item_details_header);
        itemIdInput = findViewById(R.id.item_details_id);
        itemName = findViewById(R.id.item_details_name);
        itemQuantity = findViewById(R.id.item_details_quantity);
        itemLocation = findViewById(R.id.item_details_location);
        Button saveButton = findViewById(R.id.button_edit_item);
        Button deleteButton = findViewById(R.id.button_delete_item);
        itemAlertLevel = findViewById(R.id.item_details_alert_level);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        organizationId = preferences.getString("KEY_ORGANIZATION", null);
        itemId = getIntent().getStringExtra("selected_item");
        databaseId = getIntent().getStringExtra("selected_database");

        Log.d("OnInstantiate", "ItemDetailsView " + "Organization: " + organizationId + " ItemId: " + itemId);

        if (!itemId.isEmpty()) {
            loadItemDetails(itemId); // Load item details if itemId is valid
        }

        saveButton.setOnClickListener(v -> confirmationDialog("Edit", this::onEditButtonClick));
        deleteButton.setOnClickListener(v -> confirmationDialog("Delete", this::onDeleteButtonClick));
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle back navigate to SearchView
        Intent intent;
        intent = new Intent(this, SearchView.class);
        intent.putExtra("selected_database", getIntent().getStringExtra("selected_database"));
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    /**
     * Loads and displays item details from the database.
     *
     * @param itemId The ID of the item to retrieve from the database.
     */
    private void loadItemDetails(String itemId) {
        Item item = DataManager.getInstance(ItemDetailsView.this).getItemById( databaseId, itemId);
        // Display the fetched item details
        itemHeader.setText(item.getItemName());
        itemIdInput.setText(item.getItemId());
        itemName.setText(item.getItemName());
        itemQuantity.setText(String.valueOf(item.getQuantity()));
        itemLocation.setText(item.getLocation());
        itemAlertLevel.setText(String.valueOf(item.getAlertLevel()));
        currentItem = item;

    }

    /**
     * Displays a confirmation dialog before executing an action.
     *
     * @param title The title of the dialog (e.g., "Edit" or "Delete").
     * @param action The action to perform if the user confirms.
     */
    private void confirmationDialog(String title, Runnable action){
        new AlertDialog.Builder(this)
                .setTitle("Confirm " + title)
                .setMessage("Are you sure you want to update this item?")
                .setPositiveButton("Yes", (dialog, which) -> action.run())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Handles updating an item’s details in the database.
     * Retrieves updated values from the UI fields and sends an update request to the database.
     */
    private void onEditButtonClick() {//TODO:: Should make sure that we actually made a change.
        if(currentItem.getQuantity() != Integer.parseInt(itemQuantity.getText().toString().trim())){
            Log.d(this.getClass().getSimpleName(), "onEditButtonClick: Item quantity change");
            int change = Integer.parseInt(itemQuantity.getText().toString().trim()) - currentItem.getQuantity();
            DataManager.getInstance(this).updateItemQuantity(databaseId, itemId, change);
        }

        currentItem.setItemId(itemId);  // Use existing item ID (Not editable)
        currentItem.setItemName(itemName.getText().toString().trim());
        // Don' t Set quantity or we will override our quantity change
        currentItem.setLocation(itemLocation.getText().toString().trim());
        currentItem.setAlertLevel(Integer.parseInt(itemAlertLevel.getText().toString().trim()));
        currentItem.setOrganizationId(organizationId);
        currentItem.setDatabaseId(databaseId);
        DataManager.getInstance(ItemDetailsView.this).updateItem(currentItem);
        finish();
    }

    /**
     * Handles deleting an item from the database.
     * Sends a request to remove the item and closes the activity upon success.
     */
    private void onDeleteButtonClick() {
        DataManager.getInstance(this).deleteItem(databaseId, itemId);
        finish();
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(ItemDetailsView.this, message, Toast.LENGTH_SHORT).show();
    }
}
