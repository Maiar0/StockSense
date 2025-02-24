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
 * <p>
 * This adapter manages and displays a list of `Item` objects in a RecyclerView for the search view.
 * It supports filtering the list based on a user query and handles item selection through a listener.
 * <p>
 * Features:
 * - Displays item details such as ID, name, and quantity.
 * - Filters the list of items dynamically based on user input.
 * - Supports click handling to notify listeners when an item is selected.
 * <p>
 * Usage:
 * - Attach this adapter to a RecyclerView for dynamic search and selection of items.
 * <p>
 * Dependencies:
 * - `Item` model for representing item data.
 * - `search_view_object.xml` layout for rendering individual items.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RecyclerSearchViewAdapter extends RecyclerView.Adapter<RecyclerSearchViewAdapter.ViewHolder> {

    private List<Item> filteredItemsList;
    private final OnItemSelectListener listener;

    /**
     * Constructor for the adapter.
     *
     * @param itemsList List of items to display.
     * @param listener  Listener for handling item selection.
     */
    public RecyclerSearchViewAdapter(List<Item> itemsList, OnItemSelectListener listener) {
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
        holder.itemId.setText("ID: " + item.getItemId());
        holder.itemName.setText(item.getItemName());
        holder.quantity.setText("Q: " + item.getQuantity());
        if(item.getAlertLevel() > item.getQuantity()){// TODO:: I do not like this color I need to learn more about color and theme
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.red_700, null));
        }else{
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent, null));
        }
        // Handle item selection event
        holder.itemView.setOnClickListener(v -> listener.onItemSelected(item));
    }
    // Return the size of the filtered list
    @Override
    public int getItemCount() {
        // Return size of filtered list
        return filteredItemsList.size();
    }

    /**
     * ViewHolder for individual items in the RecyclerView.
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
     * Interface for handling item selection events in search results.
     */
    public interface OnItemSelectListener {
        void onItemSelected(Item item);
    }
    public void updateData(List<Item> newItemsList) {
        this.filteredItemsList.clear();
        this.filteredItemsList.addAll(newItemsList);
        notifyDataSetChanged(); // TODO:: use more efficient method?
    }
}
