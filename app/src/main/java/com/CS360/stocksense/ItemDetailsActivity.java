package com.CS360.stocksense;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.CS360.stocksense.Database.AppDatabase;
import com.CS360.stocksense.Database.Items;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView itemHeader;
    private EditText itemQuantity, itemLocation, itemAlertLevel;
    private Button saveButton, deleteButton;
    private AppDatabase db;
    private int itemId;
    private Items currentItem;
    private String sourceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        db = AppDatabase.getInstance(this);

        itemHeader = findViewById(R.id.item_header);
        itemQuantity = findViewById(R.id.item_quantity);
        itemLocation = findViewById(R.id.item_location);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        itemAlertLevel = findViewById(R.id.item_alert_level);

        sourceActivity = getIntent().getStringExtra("source_activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId != -1) {
            loadItemDetails(itemId); // Load item details if itemId is valid
        }

        saveButton.setOnClickListener(v -> onSaveButtonClick());
        deleteButton.setOnClickListener(v -> onDeleteButtonClick());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent;
        if ("ListView".equals(sourceActivity)) {
            intent = new Intent(this, DatabaseViewActivity.class);
        } else {
            intent = new Intent(this, InventoryGridViewActivity.class);
        }
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    private void loadItemDetails(int itemId) {
        new Thread(() -> {
            currentItem = db.itemsDao().getItemById(itemId); // Fetch item by id
            runOnUiThread(() -> {
                if (currentItem != null) {
                    itemHeader.setText(currentItem.getItemName());
                    itemQuantity.setText(String.valueOf(currentItem.getQuantity()));
                    itemLocation.setText(currentItem.getLocation());
                    itemAlertLevel.setText(String.valueOf(currentItem.getAlertLevel()));
                } else {
                    showToast("Item not found");
                    finish();
                }
            });
        }).start();
    }

    private void onSaveButtonClick() {
        new Thread(() -> {
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
        }).start();
    }

    private void onDeleteButtonClick() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    new Thread(() -> {
                        db.itemsDao().delete(currentItem); // Delete item from the database
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
