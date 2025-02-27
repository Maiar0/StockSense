package com.CS360.stocksense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.DbSelectionView;
import com.CS360.stocksense.GridView;
import com.CS360.stocksense.ItemDetailsView;
import com.CS360.stocksense.MainView;
import com.CS360.stocksense.R;
import com.CS360.stocksense.RecyclerAdapters.RecyclerSearchViewAdapter;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SearchView provides a search interface for users to browse and manage items
 * within a selected database in the StockSense application.
 *
 * <p>
 * Features:
 * - Displays a list of items from a selected database in a RecyclerView.
 * - Allows users to search for items using a search box.
 * - Navigates to item details when an item is selected.
 * - Supports deleting an item by ID or exporting the database to a CSV file.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerSearchViewAdapter adapter;
    private String databaseId;
    private List<Item> fetchedItems;
    private Map<String, Item> itemIdMap = new HashMap<>();
    private Map<String, Item> itemNameMap = new HashMap<>();
    public SearchFragment(){}
    /**
     * Initializes the activity, search functionality, and RecyclerView.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize RecyclerView and EditText
        recyclerView = view.findViewById(R.id.recycler_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        EditText searchBox = view.findViewById(R.id.search_box);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Searches on handler, so we do not search unnecessarily.(300 ms)
                new Handler().postDelayed(() -> filterItems(s.toString().trim()), 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        MainView mainView = (MainView) requireActivity();
        databaseId  = mainView.getCurrentDatabaseId();

        initializeData();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "init data");
        initializeData(); // Reload data when fragment is resumed
    }

    public void initializeData() {
        List<Item> items = DataManager.getInstance(requireContext()).getItemsByDatabaseId(databaseId);
        if(items.isEmpty() ){
            Log.e(this.getClass().getSimpleName(), "InitData: Fetched: " + items.size() + "redirecting....");

            MainView mainView = (MainView) requireActivity();
            mainView.switchFragment(new DatabaseSelectionFragment());
            return;
        }
        fetchedItems = items;
        initializeHashMaps();
        Log.d(this.getClass().getSimpleName(), "InitData: Fetched " + items.size() + " Items.");
        populateRecyclerView(items);
    }
    /**
     * Initializes HashMaps for fast lookups of items by ID and name.
     * <p>
     * This method populates two HashMaps:
     * - `itemIdMap`: Maps item IDs to `Item` objects for quick retrieval.
     * - `itemNameMap`: Maps lowercase item names to `Item` objects for case-insensitive name-based searches.
     * <p>
     * This improves search performance by avoiding repeated list iterations.
     */
    private void initializeHashMaps(){
        if(fetchedItems.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "InitHashMaps: provided list is empty fetchedItems: " + fetchedItems.size() );
            return;
        }
        for (Item item : fetchedItems) {
            itemIdMap.put(item.getItemId(), item);
            itemNameMap.put(item.getItemName().toLowerCase(), item);
        }
    }

    /**
     * Updates the RecyclerView with the given items list.
     *
     * @param items List of items to display.
     */
    private void populateRecyclerView(List<Item> items) {
        if(items.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "PopulateRecyclerView: provided list is empty items: " + items.size() );
            return;
        }
        if(adapter != null){
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(adapter);
            Log.d(this.getClass().getSimpleName(), "PopulateRecyclerView: Refreshing Data: " + items.size() );
            adapter.updateData(items);
        }else{
            Log.d(this.getClass().getSimpleName(), "PopulateRecyclerView: Creating recycler: " + items.size() );
            adapter = new RecyclerSearchViewAdapter(items, this::onItemSelected);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);
        }
    }
    /**
     * Handles item selection and navigates to the item details screen.
     *
     * @param item The selected item.
     */
    private void onItemSelected(Item item) {
        Toast.makeText(requireContext(), "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        ItemDetailsFragment itemDetails = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putString("selected_database", item.getDatabaseId());
        args.putString("selected_item", item.getItemId());
        itemDetails.setArguments(args);
        MainView mainView = (MainView) requireActivity();
        mainView.switchFragment(itemDetails);
    }
    /**
     * Filters items based on the user's search query.
     * <p>
     * - If the query matches an item ID exactly, it is retrieved from `itemIdMap`.
     * - If the query partially matches an item name, matching items are retrieved from `itemNameMap`.
     * - If the query is empty, the full list of fetched items is displayed.
     *
     * @param query The user's search input.
     */
    private void filterItems(String query) {
        Log.d(this.getClass().getSimpleName(), "filterItems ");
        if (query.isEmpty()) {
            populateRecyclerView(fetchedItems);
            return;
        }

        List<Item> filteredList = new ArrayList<>();

        if (itemIdMap.containsKey(query)) {
            filteredList.add(itemIdMap.get(query)); // Exact match on ID
        } else {
            for (String key : itemNameMap.keySet()) {
                if (key.contains(query.toLowerCase())) { // Partial match on name
                    filteredList.add(itemNameMap.get(key));
                }
            }
        }
        if(filteredList.isEmpty()){
            Log.e(this.getClass().getSimpleName(), "filterItems: filteredList is empty: " + filteredList.size());
        }else{
            populateRecyclerView(filteredList);
        }
    }
}