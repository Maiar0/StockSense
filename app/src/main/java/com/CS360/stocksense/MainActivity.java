package com.CS360.stocksense;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.Utils.CSVUtils;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.io.IOException;
import java.util.List;
/**
 * MainActivity
 *
 * Serves as the base activity for the StockSense application, providing shared functionality
 * and features that are inherited by its child activities. This class includes core behaviors
 * such as navigation handling, shared data management, and reusable utility methods.
 *
 * Key Features:
 * - Shared Navigation Bar: Provides navigation buttons that child activities can configure and handle.
 * - Data Management: Fetches and manages organization-related data, including databases and items.
 * - Reusable Utility Methods: Includes common methods such as showing input dialogs, exporting data
 *   to CSV, and deleting items.
 *
 * Responsibilities:
 * - Acts as a parent class for child activities like `DbSelectionViewActivity`, `SearchViewActivity`, etc.
 * - Manages the logged-in organization and associated data (e.g., items and databases).
 * - Provides shared lifecycle methods like `initializeData` for data fetching.
 * - Exposes protected methods for child activities to implement custom behavior, such as navigation
 *   and UI actions.
 *
 * Notes:
 * - The class uses `SharedPreferences` to store and retrieve the logged-in organization's details.
 * - Navigation buttons are dynamically configured and can be customized by child activities.
 * - Handles asynchronous data fetching and error handling through callbacks.
 *
 * Example Usage:
 * - Extend `MainActivity` in a child activity to inherit navigation and data management features.
 * - Override `handleNavigationButtonClickLeft()` to define custom behavior for the left navigation button.
 *
 * Dependencies:
 * - `DataManager` for backend interactions.
 * - `CSVUtils` for exporting data to CSV files.
 * - `SharedPreferences` for persisting user session data.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class MainActivity extends AppCompatActivity {


    protected static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    protected String loggedInOrganization;
    protected List<Item> fetchedItems;
    protected List<DatabaseSelection> availableDatabases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeNavigationBar("nav1","nav2","nav3");

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        loggedInOrganization = preferences.getString("KEY_ORGANIZATION", null);

        Log.d("MainActivityLifecycle", "Organization " + loggedInOrganization);

    }
    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
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
     * @param nav1, nav2, nav3
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
     * Initializes data required for the activity.
     * This method can be overridden by child activities for specific data needs.
     */
    protected void initializeData() {
        DataManager dataManager = new DataManager();

        // Fetch organization name from shared preferences
        if (loggedInOrganization == null || loggedInOrganization.isEmpty()) {
            return;
        }

        dataManager.fetchOrganization(loggedInOrganization, new DataCallback<List<DatabaseSelection>>() {
            @Override
            public void onSuccess(List<DatabaseSelection> result) {
                availableDatabases = result; // Populate the databases list
                Log.d("InitData", "Databases loaded successfully. Total: " + availableDatabases.size());

                // Fetch databases for the logged-in organization
                for (DatabaseSelection db : availableDatabases) {
                    dataManager.fetchDatabase(loggedInOrganization, db.getId(), new DataCallback<List<Item>>() {
                        @Override
                        public void onSuccess(List<Item> result) {
                            if (fetchedItems == null) {
                                fetchedItems = result; // Initialize the items list
                            } else {
                                fetchedItems.addAll(result); // Append items
                            }
                            Log.d("InitData", "Items loaded for database: " + db.getName());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("InitData", "Error loading items for database: " + db.getName(), e);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("InitData", "Error loading databases", e);
            }
        });
    }

    /*
     * Displays a dynamic dialog for user input.
     *
     * This method allows customization of the dialog's title, input field hint, positive button text,
     * and the action performed when the positive button is clicked. It can be reused for various
     * operations by passing the appropriate parameters.
     *
     * @param title             The title of the dialog.
     * @param hint              The placeholder text for the input field.
     * @param positiveButtonText The text displayed on the positive action button (e.g., "Create", "Delete").
     * @param actionListener    A callback interface to define the action performed with the user's input.
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
     *
     * This interface is used to define the behavior when the positive button in the dialog
     * is clicked and valid input is provided by the user.
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
     *
     * This method generates a CSV file containing the data from the current list of items (`items`)
     * and saves it to the external files directory of the device. If the list is empty or null,
     * it displays an appropriate message to the user.
     *
     * The file is named `database_export.csv` and is saved in the app's external files directory.
     * The user is notified of the success or failure of the export operation via a Toast message.
     *
     * Precondition:
     * - The `items` list must be populated with valid data before calling this method.
     *
     * Postcondition:
     * - If successful, a CSV file is created and saved to the external files directory.
     * - If an error occurs (e.g., I/O failure), the user is notified, and an error is logged.
     *
     * Example Usage:
     * - Call this method when the user clicks a "Export to CSV" button.
     *
     * Error Handling:
     * - If `items` is null or empty, a Toast message notifies the user that there is no data to export.
     * - IOException is caught, and an error message is displayed.
     */
    protected void exportDatabaseToCSV() {
        if (fetchedItems == null || fetchedItems.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // Define the file path in the Downloads directory
        String fileName = "Database_" + fetchedItems.get(0).getDatabaseId();
        String filePath = getExternalFilesDir(null) + "/" + fileName;

        try {
            // Use CSVUtils to write data to the file
            CSVUtils.exportToCSV(filePath, fetchedItems);

            showToast( "Database exported to: " + filePath);
            Log.d("ExportDatabase", "Exported to " + filePath);
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
    protected void deleteItemById(String itemId, String databaseId) {
        DataManager dataManager = new DataManager();

        dataManager.deleteItem(loggedInOrganization, itemId, databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    showToast("Item deleted successfully");
                    initializeData(); // Refresh the list
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Failed to delete item: " + e.getMessage());
                    Log.e("SearchViewActivity", "Error deleting item", e);
                });
            }
        });
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
