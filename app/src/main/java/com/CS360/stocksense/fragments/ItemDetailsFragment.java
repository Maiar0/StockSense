package com.CS360.stocksense.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.CS360.stocksense.MainView;
import com.CS360.stocksense.R;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

/**
 * ItemDetailsView provides a detailed view of a selected item, allowing users to edit or delete it.
 *
 * <p>
 * Features:
 * - Displays item details retrieved from the database.
 * - Allows users to edit and save item modifications.
 * - Provides a delete option with a confirmation dialog.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */

public class ItemDetailsFragment extends Fragment {

    private TextView itemHeader, itemIdInput;
    private EditText itemQuantity, itemLocation, itemAlertLevel, itemName;
    private String itemId;
    private String organizationId;
    private String databaseId;
    private Item currentItem;
    private SupabaseRepository repository;
    public ItemDetailsFragment(){}
    /**
     * Initializes the activity and loads item details.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_details, container, false);


        itemHeader = view.findViewById(R.id.item_details_header);
        itemIdInput = view.findViewById(R.id.item_details_id);
        itemName = view.findViewById(R.id.item_details_name);
        itemQuantity = view.findViewById(R.id.item_details_quantity);
        itemLocation = view.findViewById(R.id.item_details_location);
        Button saveButton = view.findViewById(R.id.button_edit_item);
        Button deleteButton = view.findViewById(R.id.button_delete_item);
        itemAlertLevel = view.findViewById(R.id.item_details_alert_level);

        if(getArguments() != null){
            itemId = getArguments().getString("selected_item");
            databaseId = getArguments().getString("selected_database");
        }
        repository = new SupabaseRepository(requireContext());
        organizationId = repository.getOrganization();
        Log.d("OnInstantiate", "ItemDetailsFragment " + "Organization: " + organizationId + " ItemId: " + itemId);

        if (!itemId.isEmpty()) {
            loadItemDetails(itemId); // Load item details if itemId is valid
        }

        saveButton.setOnClickListener(v -> confirmationDialog("Edit", this::onEditButtonClick));
        deleteButton.setOnClickListener(v -> confirmationDialog("Delete", this::onDeleteButtonClick));
        return view;
    }



    /**
     * Loads and displays item details from the database.
     *
     * @param itemId The ID of the item to retrieve from the database.
     */
    private void loadItemDetails(String itemId) {
        Item item = DataManager.getInstance(requireContext()).getItemById( databaseId, itemId);
        // Display the fetched item details
        itemHeader.setText(item.getItemName());
        itemIdInput.setText(item.getItemId());
        itemName.setText(item.getItemName());
        itemQuantity.setText(String.valueOf(item.getQuantity()));
        itemLocation.setText(item.getLocation());
        itemAlertLevel.setText(String.valueOf(item.getAlertLevel()));
        currentItem = item;

    }

    /**
     * Displays a confirmation dialog before executing an action.
     *
     * @param title The title of the dialog (e.g., "Edit" or "Delete").
     * @param action The action to perform if the user confirms.
     */
    private void confirmationDialog(String title, Runnable action){
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm " + title)
                .setMessage("Are you sure you want to update this item?")
                .setPositiveButton("Yes", (dialog, which) -> action.run())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Handles updating an item’s details in the database.
     * Retrieves updated values from the UI fields and sends an update request to the database.
     */
    private void onEditButtonClick() {//TODO:: there is potential to change quantity twice here.
        Item previousItem = currentItem.copy();

        currentItem.setItemId(itemId);  // Use existing item ID (Not editable)
        currentItem.setItemName(itemName.getText().toString().trim());
        // Don' t Set quantity or we will override our quantity change
        currentItem.setLocation(itemLocation.getText().toString().trim());
        currentItem.setAlertLevel(Integer.parseInt(itemAlertLevel.getText().toString().trim()));
        currentItem.setOrganizationId(organizationId);
        currentItem.setDatabaseId(databaseId);
        if(currentItem.getQuantity() != Integer.parseInt(itemQuantity.getText().toString().trim())){
            Log.d(this.getClass().getSimpleName(), "onEditButtonClick: Item quantity change");
            int change = Integer.parseInt(itemQuantity.getText().toString().trim()) - currentItem.getQuantity();
            DataManager.getInstance(requireContext()).updateItemQuantity(databaseId, itemId, change);
        }
        if(previousItem.isDifferent(currentItem)){
            Log.d(this.getClass().getSimpleName(), "EditButtonClick: Sending second Call to change item: "+ itemId);
            DataManager.getInstance(requireContext()).updateItem(currentItem);
        }
        // switch to searchFragment
        SearchFragment searchFragment = new SearchFragment();
        MainView mainView = (MainView) requireActivity();
        mainView.setCurrentDatabaseId(databaseId);
        mainView.switchFragment(searchFragment);
    }

    /**
     * Handles deleting an item from the database.
     * Sends a request to remove the item and closes the activity upon success.
     */
    private void onDeleteButtonClick() {
        DataManager.getInstance(requireContext()).deleteItem(databaseId, itemId);
        // switch to searchFragment
        SearchFragment searchFragment = new SearchFragment();
        MainView mainView = (MainView) requireActivity();
        mainView.setCurrentDatabaseId(databaseId);
        mainView.switchFragment(searchFragment);
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
