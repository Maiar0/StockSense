package com.CS360.stocksense;

import static com.CS360.stocksense.MainActivity.PREFERENCES_FILE;

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

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.Supabase.SupabaseRepository;
import com.CS360.stocksense.models.Item;

import java.util.List;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView itemHeader;
    private EditText itemQuantity, itemLocation, itemAlertLevel, itemName, itemIdInput;
    private Button saveButton, deleteButton;
    private String itemId;
    private String organizationName;
    private String databaseId;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemHeader = findViewById(R.id.item_details_header);
        itemIdInput = findViewById(R.id.item_details_id);
        itemName = findViewById(R.id.item_details_name);
        itemQuantity = findViewById(R.id.item_details_quantity);
        itemLocation = findViewById(R.id.item_details_location);
        saveButton = findViewById(R.id.button_edit_item);
        deleteButton = findViewById(R.id.button_delete_item);
        itemAlertLevel = findViewById(R.id.item_details_alert_level);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        organizationName = preferences.getString("KEY_ORGANIZATION", null);
        itemId = getIntent().getStringExtra("selected_item");
        databaseId = getIntent().getStringExtra("selected_database");

        Log.d("OnInstantiate", "ItemDetailsView " + "Organization: " + organizationName + " ItemId: " + itemId);

        if (!itemId.isEmpty()) {
            loadItemDetails(organizationName, itemId); // Load item details if itemId is valid
        }

        saveButton.setOnClickListener(v -> confirmationDialog("Edit", this::onEditButtonClick));
        deleteButton.setOnClickListener(v -> confirmationDialog("Delete", this::onDeleteButtonClick));
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent;
        intent = new Intent(this, SearchViewActivity.class);
        intent.putExtra("selected_database", getIntent().getStringExtra("selected_database"));
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    private void loadItemDetails(String organizationName, String itemId) {
        SupabaseRepository repository = new SupabaseRepository();

        repository.readItem(organizationName, itemId, databaseId, new DataCallback<Item>() {
            @Override
            public void onSuccess(Item item) {
                runOnUiThread(() -> {
                    // Display the fetched item details
                    itemHeader.setText(item.getItemName());
                    itemIdInput.setText(item.getItem_id());
                    itemName.setText(item.getItemName());
                    itemQuantity.setText(String.valueOf(item.getQuantity()));
                    itemLocation.setText(item.getLocation());
                    itemAlertLevel.setText(String.valueOf(item.getAlertLevel()));
                    currentItem = item;
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ItemDetailsActivity.this, "Error loading item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ItemDetailsActivity", "Error: " + e.getMessage(), e);
                });
            }
        });
    }

    private void confirmationDialog(String title, Runnable action){
        new AlertDialog.Builder(this)
                .setTitle("Confirm " + title)
                .setMessage("Are you sure you want to update this item?")
                .setPositiveButton("Yes", (dialog, which) -> action.run())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void onEditButtonClick() {
        currentItem.setItem_id(itemId);  // Use existing item ID (Not editable)
        currentItem.setItemName(itemName.getText().toString().trim());
        currentItem.setQuantity(Integer.parseInt(itemQuantity.getText().toString().trim()));
        currentItem.setLocation(itemLocation.getText().toString().trim());
        currentItem.setAlertLevel(Integer.parseInt(itemAlertLevel.getText().toString().trim()));
        currentItem.setOrganizationName(organizationName);
        currentItem.setDatabaseId(databaseId);

        SupabaseRepository repository = new SupabaseRepository();

        repository.updateItem(organizationName, currentItem, databaseId, new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                runOnUiThread(() -> {
                    showToast("Item updated successfully!");
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Error updating item: " + e.getMessage());
                    Log.e("ItemDetailsActivity", "Update Error: " + e.getMessage(), e);
                });
            }
        });
    }


    private void onDeleteButtonClick() {
        SupabaseRepository repository = new SupabaseRepository();

        repository.deleteItem(organizationName, itemId, databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    showToast("Item deleted successfully!");
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Error deleting item: " + e.getMessage());
                    Log.e("ItemDetailsActivity", "Delete Error: " + e.getMessage(), e);
                });
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ItemDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
