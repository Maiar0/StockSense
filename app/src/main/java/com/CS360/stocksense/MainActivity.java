package com.CS360.stocksense;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.DataManager;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    public String organizationName;
    public List<Item> items;
    public List<DatabaseSelection> databases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Navigation Bar
        initNav("nav1","nav2","nav3");

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        organizationName = preferences.getString("KEY_ORGANIZATION", null);

        Log.d("OnInstantiate", "MainAcitivty " + organizationName);

        initData();
    }

    protected void onNavButton1Click() {
        // This method can be overridden in child activities
    }

    protected void onNavButton2Click() {
        // This method can be overridden in child activities
    }

    protected void onNavButton3Click() {
        // This method can be overridden in child activities
    }
    protected void initNav(String nav1, String nav2, String nav3){
        Button navButton1 = findViewById(R.id.nav_button1);
        Button navButton2 = findViewById(R.id.nav_button2);
        Button navButton3 = findViewById(R.id.nav_button3);

        navButton1.setOnClickListener(v -> onNavButton1Click()); // Set click listener for button 1
        navButton2.setOnClickListener(v -> onNavButton2Click()); // Set click listener for button 2
        navButton3.setOnClickListener(v -> onNavButton3Click()); // Set click listener for button 3

        runOnUiThread(() -> {
            navButton1.setText(nav1);
            navButton2.setText(nav2);
            navButton3.setText(nav3);
        });
        Log.d("MainActivity", "navButton1 text: " + navButton1.getText().toString());
    }
    public void initData() {
        DataManager dataManager = new DataManager();

        // Fetch organization name from shared preferences
        if (organizationName == null || organizationName.isEmpty()) {
            return;
        }

        // Load Databases
        dataManager.fetchOrganization(organizationName, new DataCallback<List<DatabaseSelection>>() {
            @Override
            public void onSuccess(List<DatabaseSelection> result) {
                databases = result; // Populate the databases list
                Log.d("InitData", "Databases loaded successfully. Total: " + databases.size());

                // Fetch items for each database
                for (DatabaseSelection db : databases) {
                    dataManager.fetchDatabase(organizationName, db.getId(), new DataCallback<List<Item>>() {
                        @Override
                        public void onSuccess(List<Item> result) {
                            if (items == null) {
                                items = result; // Initialize the items list
                            } else {
                                items.addAll(result); // Append items
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

}
