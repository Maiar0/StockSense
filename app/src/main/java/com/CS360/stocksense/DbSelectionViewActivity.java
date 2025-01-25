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
import java.util.List;

public class DbSelectionViewActivity extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerDbSlectionAdapter adapter;

    private static final int PICK_CSV_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_selection_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initNav(getString(R.string.nav_button1_dbselection), getString(R.string.nav_button2_dbselection), getString(R.string.nav_button3_dbselection));

        recyclerView = findViewById(R.id.recycler_table_view);
        loadDatabaseOptions();
        Log.d("OnInstantiate", "DbSelectionView " + organizationName);
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, LoginActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    @Override
    protected void onNavButton1Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 1 Clicked");
        showCreateDatabaseDialog();
    }
    @Override
    protected void onNavButton2Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 2 Clicked");
        showDeleteDatabaseDialog();
    }
    @Override
    protected void onNavButton3Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 3 Clicked");
        openFilePicker();
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
                showDatabaseNameDialog(uri); // Prompt for the new database name
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set-up nav buttons
        //setNavButtons(getString(R.string.nav_button1_DbSelection), getString(R.string.nav_button2_DbSelection), getString(R.string.nav_button3_DbSelection));
        loadDatabaseOptions();
    }


    private void loadDatabaseOptions() {
        DataManager dataManager = new DataManager();

        dataManager.fetchOrganization(organizationName, new DataCallback<List<DatabaseSelection>>() {
            @Override
            public void onSuccess(List<DatabaseSelection> databases) {
                runOnUiThread(() -> {
                    adapter = new RecyclerDbSlectionAdapter(databases, database -> onDatabaseSelected(database));
                    recyclerView.setLayoutManager(new LinearLayoutManager(DbSelectionViewActivity.this));
                    recyclerView.setAdapter(adapter);
                });
                for (DatabaseSelection database : databases){
                    Log.d("DbSelectionView", "name " + database.getName());
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Error loading databases: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DbSelectionView", "Error: " + e.getMessage(), e);
                });
            }
        });
    }

    private void onDatabaseSelected(DatabaseSelection database) {
        Toast.makeText(this, "Selected Database: " + database.getName(), Toast.LENGTH_SHORT).show();

        // Navigate to another activity with the selected database
        Intent intent = new Intent(DbSelectionViewActivity.this, SearchViewActivity.class);
        intent.putExtra("selected_database", database.getId());
        startActivity(intent);
    }

    private void showCreateDatabaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Database");

        // Add an input field for the database name
        final EditText input = new EditText(this);
        input.setHint("Enter database name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String databaseName = input.getText().toString().trim();
            if (!databaseName.isEmpty()) {
                createDatabase(databaseName);
            } else {
                Toast.makeText(this, "Database name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDeleteDatabaseDialog() {
        // Create an AlertDialog with an EditText input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Database");

        // Create an EditText for the input
        EditText input = new EditText(this);
        input.setHint("Enter Database ID");
        builder.setView(input);

        // Add buttons
        builder.setPositiveButton("Delete", (dialog, which) -> {
            String databaseId = input.getText().toString().trim();
            if (!databaseId.isEmpty()) {
                deletedatabase(databaseId); // Call the delete method
            } else {
                Toast.makeText(this, "Database ID cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }

    private void showDatabaseNameDialog(Uri fileUri) {
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
                Toast.makeText(this, "Database name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }



    private void createDatabase(String databaseName) {

        if (organizationName == null) {
            Toast.makeText(this, "Error: No organization name found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DataManager dataManager = new DataManager();
        dataManager.createDatabase(databaseName, organizationName, new DataCallback<Item>() {
            @Override
            public void onSuccess(Item createdDatabase) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Database created: " + createdDatabase.getDatabaseName(), Toast.LENGTH_SHORT).show();
                    loadDatabaseOptions(); // Refresh database list
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Error creating database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DbSelectionView", "Error: " + e.getMessage(), e);
                });
            }
        });
    }
    private void deletedatabase(String databaseId) {
        if (organizationName == null) {
            Toast.makeText(this, "Error: No organization name found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DataManager dataManager = new DataManager();
        dataManager.deleteDatabase(databaseId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Database deleted successfully", Toast.LENGTH_SHORT).show();
                    loadDatabaseOptions(); // Refresh the database list
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionViewActivity.this, "Error deleting database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DbSelectionView", "Error: " + e.getMessage(), e);
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
            dataManager.createItems(organizationName, items, databaseId, new DataCallback<List<Item>>() {
                @Override
                public void onSuccess(List<Item> createdItems) {
                    runOnUiThread(() -> {
                        Toast.makeText(DbSelectionViewActivity.this, "Database imported successfully", Toast.LENGTH_SHORT).show();
                        Log.d("DbSelectionView", "Created Items: " + createdItems);
                        loadDatabaseOptions(); // Refresh the UI
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(DbSelectionViewActivity.this, "Error importing database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("DbSelectionView", "Error: " + e.getMessage(), e);
                    });
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Error reading CSV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DbSelectionView", "IOException: " + e.getMessage(), e);

        } catch (RuntimeException e) {
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DbSelectionView", "RuntimeException: " + e.getMessage(), e);

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
                Log.e("DbSelectionView", "Error closing streams: " + e.getMessage(), e);
            }
        }
    }







}
