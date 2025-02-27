package com.CS360.stocksense.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.MainView;
import com.CS360.stocksense.R;
import com.CS360.stocksense.RecyclerAdapters.RecyclerGridViewAdapter;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * GridView provides a grid-based view to display items fetched from a selected database
 * in the StockSense application.
 *
 * <p>
 * Features:
 * - Displays a grid layout of items using a RecyclerView.
 * - Fetches data from a database and populates the RecyclerView.
 * - Includes navigation for transitioning between views and exporting data.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class GridFragment extends Fragment {

    private RecyclerView recyclerView;
    private String databaseId;
    private RecyclerGridViewAdapter adapter;
    private List<Item> fetchedItems;
    private Context context;
    private SupabaseRepository repository;
    /**
     * Initializes the activity, navigation bar, and RecyclerView.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
       
        recyclerView = view.findViewById(R.id.recycler_grid_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MainView mainView = (MainView) requireActivity();
        databaseId  = mainView.getCurrentDatabaseId();
        fetchedItems = new ArrayList<>();
        initializeData();
        context = requireContext();
        repository = new SupabaseRepository(context);

        return view;
    }
    
    protected void initializeData() {
        List<Item> items = DataManager.getInstance(context).getItemsByDatabaseId(databaseId);
        fetchedItems = items;
        Log.d("GridViewActivity", "Fetched " + items.size() + " databases.");
        populateRecyclerView(items);
    }
    /**
     * Updates the RecyclerView with the given items list.
     *
     * @param items List of items to display.
     */
    private void populateRecyclerView(List<Item> items) {
        if(adapter != null){
            adapter.updateData(items);
        }else{
            RecyclerGridViewAdapter adapter = new RecyclerGridViewAdapter(context, items, this::onItemSelected);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2)); // Grid layout with 2 columns
            recyclerView.setAdapter(adapter);
        }

    }

    /**
     * Displays a dialog to create a new item with input fields for item details.
     * The user enters values for item name, ID, quantity, location, and alert level.
     * After validation, the item is created and saved in the database.
     */
    private void showCreateItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create Item");

        // Layout for input fields
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Set input fields
        EditText itemName_input = new EditText(context);
        itemName_input.setHint("Enter Item Name");
        itemName_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(itemName_input);

        EditText ItemId_input = new EditText(context);
        ItemId_input.setHint("Enter item Id");
        ItemId_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(ItemId_input);

        EditText quantity_input = new EditText(context);
        quantity_input.setHint("Enter Quantity");
        quantity_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantity_input);

        EditText location_input = new EditText(context);
        location_input.setHint("Enter Location");
        location_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(location_input);

        EditText alertLevel_input = new EditText(context);
        alertLevel_input.setHint("Enter Alert level");
        alertLevel_input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(alertLevel_input);

        builder.setView(layout);

        // Set dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String itemName = itemName_input.getText().toString().trim();
            String itemId = ItemId_input.getText().toString().trim();
            String quantity = quantity_input.getText().toString().trim();//this a number
            String location = location_input.getText().toString().trim();
            String alertLevel = alertLevel_input.getText().toString().trim();

            if (itemName.isEmpty() || itemId.isEmpty() || quantity.isEmpty() || location.isEmpty() || alertLevel.isEmpty()) {
                //showToast("All fields are required.");
                return;
            }

            Item item = new Item();
            item.setItemId(itemId);
            item.setItemName(itemName);
            item.setQuantity(Integer.parseInt(quantity));
            item.setLocation(location);
            item.setAlertLevel(Integer.parseInt(alertLevel));
            item.setOrganizationId(repository.getOrganization());
            item.setDatabaseId(databaseId);
            DataManager.getInstance(context).insertItem(item);//Creates Item
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    /**
     * Handles item selection and navigates to item details.
     *
     * @param item The selected item.
     */
    private void onItemSelected(Item item) {
        Toast.makeText(context, "Selected item: " + item.getItemName(), Toast.LENGTH_SHORT).show();

        ItemDetailsFragment itemDetails = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putString("selected_database", item.getDatabaseId());
        args.putString("selected_item", item.getItemId());
        itemDetails.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, itemDetails)
                .addToBackStack(null)
                .commit();
    }
}
