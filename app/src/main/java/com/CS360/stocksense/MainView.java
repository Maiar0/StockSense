package com.CS360.stocksense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.Utils.CSVUtils;
import com.CS360.stocksense.fragments.DatabaseSelectionFragment;
import com.CS360.stocksense.fragments.GridFragment;
import com.CS360.stocksense.fragments.ItemDetailsFragment;
import com.CS360.stocksense.fragments.SearchFragment;
import com.CS360.stocksense.fragments.SettingsFragment;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;
/**
 * MainView serves as the primary activity for StockSense, providing essential
 * navigation and data management functionalities. It manages user interactions,
 * handles data updates, and facilitates navigation within the application.
 *
 * <p>
 * Features:
 * - Provides a navigation bar for switching between different views.
 * - Manages organization-related data, including databases and items.
 * - Supports data export functionality.
 * - Handles user input and dynamic dialog management.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class MainView extends AppCompatActivity implements DataManager.DataUpdateListener, NavigationView.OnNavigationItemSelectedListener  {


    protected static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    protected String organizationId;
    protected List<Item> fetchedItems;
    private String currentDatabaseId;// This is set in DatabaseSelectionsFragment
    private Button navButtonLeft, navButtonCenter, navButtonRight;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private List<DatabaseSelection> databases;
    private static final int PICK_CSV_FILE = 1;
    /**
     * Initializes the activity, sets up the navigation bar, and fetches stored preferences.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle button
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize Bottom Navigation Buttons
        navButtonLeft = findViewById(R.id.nav_button1);
        navButtonCenter = findViewById(R.id.nav_button2);
        navButtonRight = findViewById(R.id.nav_button3);

        //Loads default fragment
        if (savedInstanceState == null) {
            switchFragment( new DatabaseSelectionFragment());
            navigationView.setCheckedItem(R.id.nav_database);
        }

        //initializeNavigationBar("nav1","nav2","nav3");
        DataManager.setUpdateListener(this);
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        organizationId = preferences.getString("OrganizationId", null);

        Log.d("MainActivityLifecycle", "Organization " + organizationId);

    }
    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        updateBottomNavigation(fragment);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the drawer toggle click
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_database) {
            switchFragment( new DatabaseSelectionFragment());
        } else if (id == R.id.nav_search) {
            switchFragment( new SearchFragment());
        } else if(id == R.id.nav_settings){
            switchFragment(new SettingsFragment());
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logoutUser() {
        // Clear user session and navigate to LoginView
        SharedPreferences preferences = getSharedPreferences("com.CS360.stocksense.PREFERENCES_FILE", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(this, LoginView.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        // Close drawer if open, otherwise do default back action
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void updateBottomNavigation(Fragment fragment) {
        if(fragment instanceof ItemDetailsFragment){
            findViewById(R.id.nav_button1).setVisibility(View.GONE);
            findViewById(R.id.nav_button2).setVisibility(View.GONE);
            findViewById(R.id.nav_button3).setVisibility(View.GONE);
        }else{
            findViewById(R.id.nav_button1).setVisibility(View.VISIBLE);
            findViewById(R.id.nav_button2).setVisibility(View.VISIBLE);
            findViewById(R.id.nav_button3).setVisibility(View.VISIBLE);
        }
        if (fragment instanceof DatabaseSelectionFragment) {
            setBottomNavButtons("Create", "Delete", "Import",
                    () -> showInputDialog(
                            "Create New Database",
                            "Enter Database Name",
                            "Create",
                            input -> DataManager.getInstance(this).createNewDatabase(input) // Pass the createNewDatabase method as the action
                    ),
                    () -> showInputDialog(
                            "Delete Database",
                            "Enter Database ID",
                            "Delete",
                            input -> deleteItemById(input, null) // Pass null for itemId for "Select All"
                    ),
                    () -> openFilePicker());
        } else if (fragment instanceof SearchFragment) {
            setBottomNavButtons("Grid", "Delete", "Export",
                    () -> switchFragment(new GridFragment()),
                    () -> showInputDialog(
                            "Delete Item",
                            "Enter Item ID",
                            "Delete",
                            input -> deleteItemById(currentDatabaseId,input) // Pass the deleteDatabaseById method as the action
                    ),
                    () -> exportDatabaseToCSV());
        } else if (fragment instanceof GridFragment) {
            setBottomNavButtons("Search", "Add", "Export",
                    () -> switchFragment(new SearchFragment()),
                    () -> showCreateItemDialog(),
                    () -> exportDatabaseToCSV());
        }
    }
    private void setBottomNavButtons(String leftText, String centerText, String rightText,
                                     Runnable leftAction, Runnable centerAction, Runnable rightAction) {
        navButtonLeft.setText(leftText);
        navButtonCenter.setText(centerText);
        navButtonRight.setText(rightText);

        navButtonLeft.setOnClickListener(v -> leftAction.run());
        navButtonCenter.setOnClickListener(v -> centerAction.run());
        navButtonRight.setOnClickListener(v -> rightAction.run());
    }
    /**
     * Opens a file picker to allow the user to select a CSV file for database import.
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE);// TODO:: this needs looked into
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                showInputDialog(
                        "Import New Database",
                        "Enter Database Name",
                        "Import",
                        input -> DataManager.getInstance(this).createNewDatabase(input));
            }
        }
    }

    public String getCurrentDatabaseId(){
        return currentDatabaseId;
    }
    public void setCurrentDatabaseId(String currentDatabaseId){
        this.currentDatabaseId = currentDatabaseId;
    }
    @Override
    public void onDataUpdated() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment == null){
            Log.e("DataManager", "No active fragment to update");
            return;
        }
        if(currentFragment instanceof DatabaseSelectionFragment){
            ((DatabaseSelectionFragment) currentFragment).initializeData();
        }else if( currentFragment instanceof  SearchFragment){
            ((SearchFragment) currentFragment).initializeData();
        }
        Log.d("DataManager", "Data updated, refreshing UI. Activity: " + currentFragment.getClass().getSimpleName() );
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.getClass().getSimpleName(), "OnResume Called");
    }
    /**
     * Initializes data required for the activity. Child activities can override for specific data needs.
     */
    protected void initializeData() {
        databases = DataManager.getInstance(this).getDatabaseSelections();
        Log.d("InitData", "InitData");
    }

    /**
     * Displays a dynamic dialog for user input.
     *
     * @param title The title of the dialog.
     * @param hint The placeholder text for the input field.
     * @param positiveButtonText The text displayed on the positive action button.
     * @param actionListener A callback interface to define the action performed with the user's input.
     */
    protected void showInputDialog(String title, String hint, String positiveButtonText, DialogActionListener actionListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Add an input field for user input
        EditText input = new EditText(this);
        input.setHint(hint);
        builder.setView(input);

        // Set up the positive button with a dynamic listener
        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            String inputValue = input.getText().toString().trim();
            if (!inputValue.isEmpty()) {
                actionListener.onAction(inputValue); // Trigger the provided action
            } else {
                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the negative button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }

    /**
     * Interface for handling dialog actions.
     */
    protected interface DialogActionListener {
        /**
         * Defines the action to be performed using the input provided by the user.
         *
         * @param input The input provided by the user in the dialog.
         */
        void onAction(String input);
    }
    /**
     * Exports the current database items to a CSV file.
     */
    protected void exportDatabaseToCSV() {
        if (fetchedItems == null || fetchedItems.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "Database_" + fetchedItems.get(0).getDatabaseId() + ".csv";

        try {
            CSVUtils.exportToCSV(this, fileName, fetchedItems);
            showToast("Database exported to Downloads: " + fileName);
        } catch (IOException e) {
            showToast("Failed to export database: " + e.getMessage());
            Log.e("ExportDatabase", "Error exporting database", e);
        }
    }
    /**
     * Deletes an item from the database by its ID.
     *
     * @param itemId The unique identifier of the item to be deleted.
     * @param databaseId The ID of the database containing the item.
     */
    public void deleteItemById(String databaseId, String itemId) {
        DataManager.getInstance(this).deleteItem(databaseId, itemId);
        initializeData();
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
            item.setDatabaseId(currentDatabaseId);
            DataManager.getInstance(this).insertItem(item);//Creates Item
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
