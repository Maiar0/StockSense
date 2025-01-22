package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.nav_button1).setOnClickListener(v -> onNavButton1Click()); // Set click listener for button 1
        findViewById(R.id.nav_button2).setOnClickListener(v -> onNavButton2Click()); // Set click listener for button 2
        findViewById(R.id.nav_button3).setOnClickListener(v -> onNavButton3Click()); // Set click listener for button 3

        Log.d("OnInstantiate", "MainAcitivty ");
    }

    protected void onNavButton1Click() {
        // Navigate to InventoryGridViewActivity
        Intent intent = new Intent(this, GridViewActivity.class);
        startActivity(intent);
        Log.d("MainActivity", "Navigated to InventoryGridViewActivity");
    }

    protected void onNavButton2Click() {
        // Navigate to DatabaseViewActivity
        Intent intent = new Intent(this, TableViewActivity.class);
        startActivity(intent);
        Log.d("MainActivity", "Navigated to DatabaseViewActivity");
    }

    protected void onNavButton3Click() {
        // Show dialog to create a new item
        showCreateItemDialog();
        Log.d("MainActivity", "Create Item dialog shown");
    }

    private void showCreateItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Item");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_item, null);
        builder.setView(view);

        TextInputEditText itemIdInput = view.findViewById(R.id.item_id_input);
        TextInputEditText itemNameInput = view.findViewById(R.id.item_name_input);
        TextInputEditText itemQuantityInput = view.findViewById(R.id.item_quantity_input);
        TextInputEditText itemLocationInput = view.findViewById(R.id.item_location_input);
        TextInputEditText alertLevelInput = view.findViewById(R.id.alert_level_input);

        builder.setPositiveButton("Create", null); // Set to null to prevent automatic dismissal

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button createButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            createButton.setOnClickListener(view1 -> {
                // Validate inputs
                String itemIdStr = itemIdInput.getText().toString();
                String itemName = itemNameInput.getText().toString();
                String itemQuantityStr = itemQuantityInput.getText().toString();
                String itemLocation = itemLocationInput.getText().toString();
                String alertLevelStr = alertLevelInput.getText().toString();

                if (itemIdStr.isEmpty() || itemName.isEmpty() || itemQuantityStr.isEmpty() || itemLocation.isEmpty() || alertLevelStr.isEmpty()) {
                    // Show error message if any field is empty
                    Toast.makeText(MainActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int itemId = Integer.parseInt(itemIdStr);
                        int itemQuantity = Integer.parseInt(itemQuantityStr);
                        int alertLevel = Integer.parseInt(alertLevelStr);

                        createNewItem(itemId, itemName, itemQuantity, itemLocation, alertLevel); // Create new item
                        dialog.dismiss(); // Dismiss the dialog if creation is successful
                    } catch (NumberFormatException e) {
                        // Show error message if number parsing fails
                        Toast.makeText(MainActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private void createNewItem(int id, String name, int quantity, String location, int alertLevel) {
        new Thread(() -> {
            //TODO: CREATE ITEM IMPLEMENTAION
            runOnUiThread(() -> {
                onNewItemCreated(); // Notify that a new item has been created
                showToast("Item created successfully"); // Show success message
            });
        }).start();
    }

    protected void onNewItemCreated() {
        // This method can be overridden in child activities if needed
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.show(); // Show toast message
    }
}
