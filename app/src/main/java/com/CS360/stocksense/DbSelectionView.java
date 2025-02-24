package com.CS360.stocksense;

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

import com.CS360.stocksense.RecyclerAdapters.RecyclerDbSelectionAdapter;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.Utils.CSVUtils;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * DbSelectionView provides an interface for users to manage databases in the StockSense application.
 * It allows viewing, creating, deleting, and importing databases via a RecyclerView.
 *
 * <p>
 * Features:
 * - Displays available databases in a RecyclerView.
 * - Allows users to create and delete databases.
 * - Supports importing databases from CSV files.
 * - Navigates to `SearchViewActivity` when a database is selected.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class DbSelectionView extends MainView {

    private RecyclerView recyclerView;
    private RecyclerDbSelectionAdapter adapter;

    private static final int PICK_CSV_FILE = 1;
    /**
     * Initializes the activity and sets up navigation and data display.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_selection_view);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initializeNavigationBar(
                getString(R.string.nav_button1_dbselection),
                getString(R.string.nav_button2_dbselection),
                getString(R.string.nav_button3_dbselection)
        );

        recyclerView = findViewById(R.id.recycler_table_view);
        initializeData();
        Log.d("DbSelectionViewActivity", "Organization " + organizationId);
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Handle back navigation to login screen
        Intent intent = new Intent(this, LoginView.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void handleNavigationButtonClickLeft(){
        // Prompt user to create a new database
        showInputDialog(
                "Create New Database",
                "Enter Database Name",
                "Create",
                input -> createDatabase(input) // Pass the createDatabase method as the action
        );
    }
    @Override
    protected void handleNavigationButtonClickCenter(){
        // Prompt user to delete a database by ID

        showInputDialog(
                "Delete Database",
                "Enter Database ID",
                "Delete",
                input -> deleteItemById(input, null) // Pass null for itemId for "Select All"
        );
    }
    @Override
    protected void handleNavigationButtonClickRight(){
        // Open file picker for CSV import
        openFilePicker();
    }
    /**
     * Initializes data by fetching databases and populating the RecyclerView.
     */
    @Override
    protected void initializeData() {
        List<DatabaseSelection> databases = DataManager.getInstance(DbSelectionView.this).getDatabaseSelections();
        Log.d("DbSelectionViewActivity", "Fetched " + databases.size() + " databases.");
        populateRecyclerView(databases);
    }

    /**
     * Updates the RecyclerView with the given database list.
     *
     * @param databases List of databases to display.
     */
    private void populateRecyclerView(List<DatabaseSelection> databases) {
        if(adapter != null){
            adapter.updateData(databases);
        }else{
            adapter = new RecyclerDbSelectionAdapter(databases, this::onDatabaseSelected);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

    }

    /**
     * Creates a new database and adds it to the system.
     *
     * @param databaseName The name of the new database.
     */
    private void createDatabase(String databaseName) {
        DataManager.getInstance(DbSelectionView.this).createNewDatabase(databaseName);
        initializeData();
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
                showImportDatabaseDialog(uri); // Prompt for the new database name
            }
        }
    }

    /**
     * Displays a confirmation dialog for importing a database from a CSV file.
     *
     * @param fileUri The URI of the selected CSV file.
     */
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



    /**
     * Imports database records from a CSV file.
     *
     * @param fileUri The URI of the CSV file.
     * @param dbName The name of the database to create.
     */
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
            Log.d("DbSelectionView", "Import Method: items: " + items);
            DataManager.getInstance(DbSelectionView.this).importNewDatabase(dbName,items);



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

    /**
     * Handles the selection of a database and navigates to the `SearchViewActivity`.
     *
     * @param database The selected database.
     */
    private void onDatabaseSelected(DatabaseSelection database) {
        Intent intent = new Intent(this, SearchView.class);
        intent.putExtra("selected_database", database.getId());
        startActivity(intent);
    }
}
