package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GridViewActivity extends SearchViewActivity {

    private RecyclerView recyclerView;
    private RecyclerGridViewAdapter adapter;
    private String databaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        initNav(getString(R.string.nav_button1_grid), getString(R.string.nav_button2_grid), getString(R.string.nav_button3_search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_grid_view);

        Log.d("OnInstantiate", "GridView " + organizationName + ' ' + databaseId);

        databaseId = getIntent().getStringExtra("selected_database");
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, DbSelectionViewActivity.class);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }
    protected void onNavButton1Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 1 Clicked");
        Intent intent = new Intent(this, SearchViewActivity.class);
        intent.putExtra("selected_database", databaseId);
        startActivity(intent);
    }
    @Override
    protected void onNavButton2Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 2 Clicked");

    }
    @Override
    protected void onNavButton3Click(){
        super.onNavButton1Click();
        Log.d("DbSelectionView", "Nav 3 Clicked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void initData() {
        DataManager dataManager = new DataManager();

        dataManager.fetchDatabase(organizationName, databaseId, new DataCallback<List<Item>>() {
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
