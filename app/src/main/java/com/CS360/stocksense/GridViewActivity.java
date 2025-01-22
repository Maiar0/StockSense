package com.CS360.stocksense;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GridViewActivity extends MainActivity {

    private RecyclerView recyclerView;
    private RecyclerGridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        recyclerView = findViewById(R.id.recycler_grid_view);

        findViewById(R.id.nav_button2).setOnClickListener(v -> onNavButton2Click());
        findViewById(R.id.nav_button3).setOnClickListener(v -> onNavButton3Click());
        Log.d("OnInstantiate", "GridView ");

        loadData(); // Load data from the database
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(() -> {
            List<Item> currentItems = adapter.getItemsList();
            for (Item item : currentItems) {
                //TODO:: // Update items in the database
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Reload data when resuming activity
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
                    if (adapter == null) {
                        adapter = new RecyclerGridViewAdapter(items, GridViewActivity.this);
                        recyclerView.setLayoutManager(new GridLayoutManager(GridViewActivity.this, 2));
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateData(items);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(GridViewActivity.this, "Error loading items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private List<Item> sortData(List<Item> itemsList) {
        Collections.sort(itemsList, Comparator.comparing(Item::getItemName)); // Sort items by name
        return itemsList;
    }
}
