package com.CS360.stocksense.RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.ItemDetailsActivity;
import com.CS360.stocksense.R;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.List;
/**
 * RecyclerGridViewAdapter
 *
 * This adapter is responsible for managing and displaying a list of items
 * in a grid-based RecyclerView. It handles data binding, user interactions,
 * and navigation to detailed item views.
 *
 * Features:
 * - Displays item details: name, quantity, and location.
 * - Allows users to increment or decrement item quantities.
 * - Supports navigation to an `ItemDetailsActivity` for detailed item information.
 *
 * Usage:
 * - Attach this adapter to a RecyclerView to display a list of `Item` objects in a grid layout.
 *
 * Dependencies:
 * - `ItemDetailsActivity` for item-specific navigation.
 * - `Item` model for managing item data.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {

    private List<Item> itemsList;
    private RecyclerSearchViewAdapter.OnItemSelectListener listener;
    private Context context;
    /**
     * Constructor for the adapter.
     *
     * @param itemsList List of Item objects to display.
     * @param listener   Context for launching activities.
     */
    public RecyclerGridViewAdapter(Context context, List<Item> itemsList, RecyclerSearchViewAdapter.OnItemSelectListener listener) {
        this.context = context;
        this.itemsList = itemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder
        Item item = itemsList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemQuantity.setText("Q: " + item.getQuantity());
        holder.itemLocation.setText(item.getLocation());

        // Increment item quantity on button click
        holder.incrementButton.setOnClickListener(v -> {
            DataManager.getInstance(context).updateItemQuantity(item.getDatabaseId(), item.getItemId(), 1);
            notifyItemChanged(position);
        });

        // Decrement item quantity on button click
        holder.decrementButton.setOnClickListener(v -> {
            DataManager.getInstance(context).updateItemQuantity(item.getDatabaseId(), item.getItemId(), -1);
            notifyItemChanged(position);
        });

        // Handle item selection event
        holder.itemView.setOnClickListener(v -> listener.onItemSelected(item));
    }
    /**
     * Interface for handling item selection events in the RecyclerView.
     */
    public interface OnItemSelectListener {
        void onItemSelected(Item item);
    }
    @Override
    public int getItemCount() {
        // Return total number of items
        return itemsList.size();
    }

    /**
     * ViewHolder for individual grid items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemLocation; // Item details
        Button incrementButton, decrementButton; // Buttons for quantity control

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemLocation = itemView.findViewById(R.id.item_location);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
        }
    }
}
