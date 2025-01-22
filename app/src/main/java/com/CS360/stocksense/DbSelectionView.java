package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.models.DatabaseSelection;

import java.util.List;

public class DbSelectionView extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerDbSlectionAdapter adapter;
    private String organizationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        recyclerView = findViewById(R.id.recycler_grid_view);

        organizationName = getSharedPreferences("com.CS360.stocksense.PREFERENCES_FILE", MODE_PRIVATE).getString("KEY_ORGANIZATION", null);

        Log.d("OnInstantiate", "Initialized" + "KEY_ORGANIZATION " + organizationName);

        // Load and display database options
        loadDatabaseOptions();
    }

    private void loadDatabaseOptions() {

        if (organizationName == null) {
            Toast.makeText(this, "Organization not found. Please log in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DataManager dataManager = new DataManager();

        dataManager.loadDatabases(organizationName, new DataCallback<List<DatabaseSelection>>() {
            @Override
            public void onSuccess(List<DatabaseSelection> databases) {
                runOnUiThread(() -> {
                    adapter = new RecyclerDbSlectionAdapter(databases, database -> onDatabaseSelected(database));
                    recyclerView.setLayoutManager(new LinearLayoutManager(DbSelectionView.this));
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(DbSelectionView.this, "Error loading databases: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DbSelectionView", "Error: " + e.getMessage(), e);
                });
            }
        });
    }


    private void onDatabaseSelected(DatabaseSelection database) {
        Toast.makeText(this, "Selected Database: " + database.getName(), Toast.LENGTH_SHORT).show();

        // Navigate to another activity with the selected database
        Intent intent = new Intent(DbSelectionView.this, TableViewActivity.class);
        intent.putExtra("selected_database", database.getId());
        startActivity(intent);
    }
}
