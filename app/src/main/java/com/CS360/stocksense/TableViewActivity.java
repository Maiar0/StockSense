package com.CS360.stocksense;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.List;

public class TableViewActivity extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerTableViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);

        recyclerView = findViewById(R.id.recycler_table_view);

        findViewById(R.id.nav_button1).setOnClickListener(v -> onNavButton1Click());
        findViewById(R.id.nav_button3).setOnClickListener(v -> onNavButton3Click());
        Log.d("OnInstantiate", "TableView ");
        loadData(); // Load data from the database
    }

    @Override
    protected void onNewItemCreated() {
        super.onNewItemCreated();
        loadData(); // Reload data when a new item is created
    }

    private void loadData() {
        DataManager dataManager = new DataManager();

        dataManager.loadItems(new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                runOnUiThread(() -> {
                    adapter = new RecyclerTableViewAdapter(items, item -> showDeleteConfirmationDialog(item));
                    recyclerView.setLayoutManager(new LinearLayoutManager(TableViewActivity.this));
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(TableViewActivity.this, "Error loading items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void showDeleteConfirmationDialog(Item current) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem(current))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteItem(Item item) {
        //TODO Implement Method
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(TableViewActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
