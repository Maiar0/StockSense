/**
 * DbSelectionViewActivity allows users to view, create, delete, and import databases.
 * It inherits shared navigation and data management functionality from MainActivity.
 */
package com.CS360.stocksense;

import static com.CS360.stocksense.Utils.Utils.generateDatabaseId;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Utils.CSVUtils;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DbSelectionViewActivity extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerDbSelectionAdapter adapter;

    private static final int PICK_CSV_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_selection_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeNavigationBar(
                getString(R.string.nav_button1_dbselection),
                getString(R.string.nav_button2_dbselection),
                getString(R.string.nav_button3_dbselection)
        );

        recyclerView = findViewById(R.id.recycler_table_view);
        initializeData();
        Log.d("DbSelectionViewActivity", "Organization " + loggedInOrganization);
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, LoginViewActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void handleNavigationButtonClickLeft(){
        showInputDialog(
                "Create New Database",
                "Enter Database Name",
                "Create",
                input -> createDatabase(input) // Pass the createDatabase method as the action
        );
    }
    @Override
    protected void handleNavigationButtonClickCenter(){
        showInputDialog(
                "Delete Database",
                "Enter Database ID",
                "Delete",
                input -> deleteDatabaseById(input) // Pass the deleteDatabaseById method as the action
        );
    }
    @Override
    protected void handleNavigationButtonClickRight(){
        openFilePicker();
    }
    /**
     * Initializes data by fetching databases and populating the RecyclerView.
     */
    @Override
    protected void initializeData() {
        DataManager dataManager = new DataManager();

        dataManager.fetchOrganization(loggedInOrganization, new DataCallback<List<DatabaseSelection>>() {
            @Override
            public void onSuccess(List<DatabaseSelection> databases) {
                Log.d("DbSelectionViewActivity", "Fetched " + databases.size() + " databases.");
                populateRecyclerView(databases); // Populate RecyclerView after data is fetched
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading databases: " + e.getMessage());
                Log.e("DbSelectionViewActivity", "Error fetching databases", e);
            }
        });
    }

    /**
     * Populates the RecyclerView with the given list of databases.
     *
     * @param databases List of DatabaseSelection objects to display.
     */
    private void populateRecyclerView(List<DatabaseSelection> databases) {
        adapter = new RecyclerDbSelectionAdapter(databases, this::onDatabaseSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void createDatabase(String databaseName) {
        if (loggedInOrganization == null) {
            showToast( "Error: No organization name found. Please log in again.");
            return;
        }

        // Create an Item for the Database to hold
        Item newDatabaseItem = new Item();
        newDatabaseItem.setDatabaseName(databaseName);
        newDatabaseItem.setOrganizationName(loggedInOrganization);
        newDatabaseItem.setDatabaseId(generateDatabaseId());

        List<Item> items = new ArrayList<>();
        items.add(newDatabaseItem);

        DataManager dataManager = new DataManager();
        dataManager.createItems(loggedInOrganization, items, newDatabaseItem.getDatabaseId(), new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> createdItems) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Database created successfully: " + databaseName, Toast.LENGTH_SHORT).show();
                    initializeData(); // Refresh the database list
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Error creating database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DbSelectionViewActivity", "Error: " + e.getMessage(), e);
                });
            }
        });
    }


    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                showImportDatabaseDialog(uri); // Prompt for the new database name
            }
        }
    }
    private void showImportDatabaseDialog(Uri fileUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Database Name");

        EditText input = new EditText(this);
        input.setHint("Database Name");
        builder.setView(input);

        builder.setPositiveButton("Import", (dialog, which) -> {
            String dbName = input.getText().toString().trim();
            if (!dbName.isEmpty()) {
                importDatabase(fileUri, dbName); // Handle the import
            } else {
                showToast("Database name cannot be empty");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void deleteDatabaseById(String databaseId) {
        if (loggedInOrganization == null) {
            showToast("Error: No organization name found. Please log in again.");
            return;
        }

        DataManager dataManager = new DataManager();
        dataManager.deleteDatabase(databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                runOnUiThread(() -> {
                    showToast("Database deleted successfully");
                    initializeData();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showToast("Error deleting database: " + e.getMessage());
                    Log.e("DbSelectionViewActivity", "Error: " + e.getMessage(), e);
                });
            }
        });
    }
    private void importDatabase(Uri fileUri, String dbName) {
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                throw new IOException("Failed to open input stream for the selected file.");
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Parse CSV file
            List<Item> items = CSVUtils.importFromCSV(reader);
            for(Item item : items){
                item.setItemName(dbName);
            }
            // Insert items into the database using createItems
            String databaseId = generateDatabaseId();
            DataManager dataManager = new DataManager();
            dataManager.createItems(loggedInOrganization, items, databaseId, new DataCallback<List<Item>>() {
                @Override
                public void onSuccess(List<Item> createdItems) {
                    runOnUiThread(() -> {
                        showToast("Database imported successfully");
                        Log.d("DbSelectionViewActivity", "Created Items: " + createdItems);
                        initializeData(); // Refresh the UI
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        showToast("Error importing database: " + e.getMessage());
                        Log.e("DbSelectionViewActivity", "Error: " + e.getMessage(), e);
                    });
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Error reading CSV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DbSelectionViewActivity", "IOException: " + e.getMessage(), e);

        } catch (RuntimeException e) {
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DbSelectionViewActivity", "RuntimeException: " + e.getMessage(), e);

        } finally {
            // Ensure resources are closed
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e("DbSelectionViewActivity", "Error closing streams: " + e.getMessage(), e);
            }
        }
    }
    private void onDatabaseSelected(DatabaseSelection database) {
        // Navigate to another activity with the selected database
        Intent intent = new Intent(this, SearchViewActivity.class);
        intent.putExtra("selected_database", database.getId());
        startActivity(intent);
    }
}
