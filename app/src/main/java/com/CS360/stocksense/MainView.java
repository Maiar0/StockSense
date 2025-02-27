package com.CS360.stocksense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;


import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.Utils.CSVUtils;
import com.CS360.stocksense.fragments.DatabaseSelectionFragment;
import com.CS360.stocksense.fragments.SearchFragment;
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
    private String currentDatabaseId;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
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

        //Loads default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DatabaseSelectionFragment())
                    .commit();
        }

        //initializeNavigationBar("nav1","nav2","nav3");
        DataManager.setUpdateListener(this);
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        organizationId = preferences.getString("OrganizationId", null);

        Log.d("MainActivityLifecycle", "Organization " + organizationId);

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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DatabaseSelectionFragment())
                    .commit();
        } else if (id == R.id.nav_search) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SearchFragment())
                    .commit();
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

    public String getCurrentDatabaseId(){
        return currentDatabaseId;
    }
    public void setCurrentDatabaseId(String currentDatabaseId){
        this.currentDatabaseId = currentDatabaseId;
    }
    @Override
    public void onDataUpdated() {
        runOnUiThread(() -> {
            Log.d("DataManager", "Data updated, refreshing UI. Activity: " + this.getClass().getSimpleName() );
            initializeData();
        });
    }
    @Override
    protected void onResume() {

        super.onResume();
        Log.d(this.getClass().getSimpleName(), "OnResume Called");
    }
    /**
     * Handles clicks on navigation buttons. Child activities can override this to implement
     * specific navigation behavior.
     *
     */
    protected void handleNavigationButtonClickLeft() {
        // This method can be overridden in child activities
    }
    /**
     * Handles clicks on navigation buttons. Child activities can override this to implement
     * specific navigation behavior.
     *
     */
    protected void handleNavigationButtonClickCenter() {
        // This method can be overridden in child activities
    }
    /**
     * Handles clicks on navigation buttons. Child activities can override this to implement
     * specific navigation behavior.
     *
     */
    protected void handleNavigationButtonClickRight() {
        // This method can be overridden in child activities
    }
    /**
     * Initializes the navigation bar with the given button titles.
     *
     * @param nav1 Title of the first navigation button.
     * @param nav2 Title of the second navigation button.
     * @param nav3 Title of the third navigation button.
     */
    protected void initializeNavigationBar(String nav1, String nav2, String nav3){
        Button navButton1 = findViewById(R.id.nav_button1);
        Button navButton2 = findViewById(R.id.nav_button2);
        Button navButton3 = findViewById(R.id.nav_button3);

        navButton1.setOnClickListener(v -> handleNavigationButtonClickLeft()); // Set click listener for button 1
        navButton2.setOnClickListener(v -> handleNavigationButtonClickCenter()); // Set click listener for button 2
        navButton3.setOnClickListener(v -> handleNavigationButtonClickRight()); // Set click listener for button 3

        runOnUiThread(() -> {
            navButton1.setText(nav1);
            navButton2.setText(nav2);
            navButton3.setText(nav3);
        });
        Log.d("MainActivity", "navButton1 text: " + navButton1.getText().toString());
    }
    /**
     * Initializes data required for the activity. Child activities can override for specific data needs.
     */
    protected void initializeData() {
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
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
