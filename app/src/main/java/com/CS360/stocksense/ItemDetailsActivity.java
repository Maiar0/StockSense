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
import com.CS360.stocksense.models.Item;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView itemHeader;
    private EditText itemQuantity, itemLocation, itemAlertLevel;
    private Button saveButton, deleteButton;
    private int itemId;
    private String organizationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemHeader = findViewById(R.id.item_details_header);
        itemQuantity = findViewById(R.id.item_details_quantity);
        itemLocation = findViewById(R.id.item_details_location);
        saveButton = findViewById(R.id.button_edit_item);
        deleteButton = findViewById(R.id.button_delete_item);
        itemAlertLevel = findViewById(R.id.item_details_alert_level);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        organizationName = preferences.getString("KEY_ORGANIZATION", null);
        itemId = getIntent().getIntExtra("selected_item", -1);

        Log.d("OnInstantiate", "ItemDetailsView " + "Organization: " + organizationName + " ItemId: " + itemId);

        if (itemId != -1) {
            loadItemDetails(organizationName, itemId); // Load item details if itemId is valid
        }

        saveButton.setOnClickListener(v -> onSaveButtonClick());
        deleteButton.setOnClickListener(v -> onDeleteButtonClick());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent;
        intent = new Intent(this, SearchViewActivity.class);
        intent.putExtra("selected_database", getIntent().getStringExtra("selected_database"));
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    private void loadItemDetails(String organizationName, int itemId) {
        DataManager dataManager = new DataManager();

        dataManager.fetchSingleItem(organizationName, itemId, new DataCallback<Item>() {
            @Override
            public void onSuccess(Item item) {
                runOnUiThread(() -> {
                    // Display the fetched item details
                    itemHeader.setText(item.getItemName());
                    itemQuantity.setText(String.valueOf(item.getQuantity()));
                    itemLocation.setText(item.getLocation());
                    itemAlertLevel.setText(String.valueOf(item.getAlertLevel()));
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

    private void onSaveButtonClick() {
        //TODO:: Implement
        /*new Thread(() -> {
            try {
                int currentQuantity = currentItem.getQuantity();
                int newQuantity = Integer.parseInt(itemQuantity.getText().toString());
                int alertLevel = Integer.parseInt(itemAlertLevel.getText().toString());

                currentItem.setQuantity(newQuantity);
                currentItem.setLocation(itemLocation.getText().toString());
                currentItem.setAlertLevel(alertLevel);
                db.itemsDao().update(currentItem); // Update item in the database

                runOnUiThread(() -> showToast("Item updated successfully"));
                NavUtils.navigateUpFromSameTask(this);
            } catch (NumberFormatException e) {
                runOnUiThread(() -> showToast("Invalid number format"));
            } catch (Exception e) {
                runOnUiThread(() -> showToast("Error saving item"));
            }
        }).start();*/
    }

    private void onDeleteButtonClick() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    new Thread(() -> {
                        //TODO:: // Delete item from the database
                        runOnUiThread(() -> {
                            showToast("Item deleted successfully");
                            finish();
                        });
                    }).start();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(ItemDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
