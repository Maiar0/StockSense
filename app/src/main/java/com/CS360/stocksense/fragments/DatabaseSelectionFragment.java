package com.CS360.stocksense.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.MainView;
import com.CS360.stocksense.R;
import com.CS360.stocksense.RecyclerAdapters.RecyclerDbSelectionAdapter;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;
import com.CS360.stocksense.Utils.CSVUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * DatabaseSelectionFragment provides an interface for users to manage databases in the StockSense application.
 * It allows viewing, creating, deleting, and importing databases via a RecyclerView.
 *
 * <p>
 * Features:
 * - Displays available databases in a RecyclerView.
 * - Allows users to create and delete databases.
 * - Supports importing databases from CSV files.
 * - Navigates to `SearchFragment` when a database is selected.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class DatabaseSelectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerDbSelectionAdapter adapter;
    private String organizationId;

    private static final int PICK_CSV_FILE = 1;

    public DatabaseSelectionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database_selection, container, false);

        recyclerView = view.findViewById(R.id.recycler_table_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        initializeData();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "Re-init data");
        initializeData(); // Reload data when fragment is resumed
    }

    /**
     * Fetches databases and populates the RecyclerView.
     */
    private void initializeData() {
        List<DatabaseSelection> databases = DataManager.getInstance(requireContext()).getDatabaseSelections();
        Log.d("DatabaseSelectionFragment", "Fetched " + databases.size() + " databases.");
        populateRecyclerView(databases);
    }

    /**
     * Populates the RecyclerView with database selections.
     * @param databases List of databases to display.
     */
    private void populateRecyclerView(List<DatabaseSelection> databases) {
        if (adapter != null) {
            Log.d(this.getClass().getSimpleName(), "PopulateRecyclerView: Refreshing Data: " + databases.size() );
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(adapter);
            adapter.updateData(databases);
        } else {
            Log.d(this.getClass().getSimpleName(), "PopulateRecyclerView: Creating recycler: " + databases.size() );
            adapter = new RecyclerDbSelectionAdapter(databases, this::onDatabaseSelected);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Handles database selection and navigates to SearchFragment.
     * @param database The selected database.
     */
    private void onDatabaseSelected(DatabaseSelection database) {
        SearchFragment searchFragment = new SearchFragment();
        MainView mainView = (MainView) requireActivity();
        mainView.setCurrentDatabaseId(database.getId());

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, searchFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Opens a file picker to allow the user to select a CSV file for database import.
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE);
    }

    /**
     * Handles the result from file picker.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                showImportDatabaseDialog(uri);
            }
        }
    }

    /**
     * Displays a dialog to prompt the user for the new database name.
     */
    private void showImportDatabaseDialog(Uri fileUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Database Name");

        EditText input = new EditText(requireContext());
        input.setHint("Database Name");
        builder.setView(input);

        builder.setPositiveButton("Import", (dialog, which) -> {
            String dbName = input.getText().toString().trim();
            if (!dbName.isEmpty()) {
                importDatabase(fileUri, dbName);
            } else {
                showToast("Database name cannot be empty");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Imports database records from a CSV file.
     */
    private void importDatabase(Uri fileUri, String dbName) {
        try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            List<Item> items = CSVUtils.importFromCSV(reader);
            Log.d("DatabaseSelectionFragment", "Import Method: items: " + items);
            DataManager.getInstance(requireContext()).importNewDatabase(dbName, items);

        } catch (IOException e) {
            showToast("Error reading CSV file: " + e.getMessage());
            Log.e("DatabaseSelectionFragment", "IOException: " + e.getMessage(), e);
        }
    }

    /**
     * Displays a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
