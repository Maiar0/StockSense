package com.CS360.stocksense.RecyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.R;
import com.CS360.stocksense.models.Item;
import java.util.ArrayList;
import java.util.List;
/**
 * RecyclerSearchViewAdapter
 *
 * This adapter manages and displays a list of `Item` objects in a RecyclerView for the search view.
 * It supports filtering the list based on a user query and handles item selection through a listener.
 *
 * Features:
 * - Displays item details such as ID, name, and quantity.
 * - Filters the list of items dynamically based on user input.
 * - Supports click handling to notify listeners when an item is selected.
 *
 * Usage:
 * - Attach this adapter to a RecyclerView for dynamic search and selection of items.
 *
 * Dependencies:
 * - `Item` model for representing item data.
 * - `search_view_object.xml` layout for rendering individual items.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RecyclerSearchViewAdapter extends RecyclerView.Adapter<RecyclerSearchViewAdapter.ViewHolder> {

    private List<Item> itemsList;
    private List<Item> filteredItemsList;
    private OnItemSelectListener listener;

    /**
     * Constructor for the adapter.
     *
     * @param itemsList List of items to display.
     * @param listener  Listener for handling item selection.
     */
    public RecyclerSearchViewAdapter(List<Item> itemsList, OnItemSelectListener listener) {
        this.itemsList = itemsList;
        this.filteredItemsList = new ArrayList<>(itemsList); // Initialize with all items
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data from the filtered list to the ViewHolder
        Item item = filteredItemsList.get(position);
        holder.itemId.setText("ID: " + item.getItem_id());
        holder.itemName.setText(item.getItemName());
        holder.quantity.setText("Q: " + item.getQuantity());

        // Set a click listener to notify the listener when an item is selected
        holder.itemView.setOnClickListener(v -> listener.onItemSelected(item));
    }
    // Return the size of the filtered list
    @Override
    public int getItemCount() {
        return filteredItemsList.size();
    }

    /**
     * ViewHolder for individual items in the RecyclerView.
     * Holds references to the item's details.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemId, itemName, quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            itemId = itemView.findViewById(R.id.item_id);
            itemName = itemView.findViewById(R.id.item_name);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

    /**
     * Interface for handling item selection events.
     */
    public interface OnItemSelectListener {
        void onItemSelected(Item item);
    }
}
