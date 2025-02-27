package com.CS360.stocksense.RecyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.R;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.models.Item;

import java.util.List;
/**
 * RecyclerGridViewAdapter is responsible for managing and displaying a list of items
 * in a grid-based RecyclerView. It provides UI interactions for quantity modification
 * and item selection.
 *
 * <p>
 * Features:
 * - Displays item details: name, quantity, and location.
 * - Allows users to increment or decrement item quantities.
 * - Highlights items when quantity is below the alert level.
 * - Supports item selection via click events.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {

    private List<Item> itemsList;
    private final OnItemSelectListener listener;
    private final Context context;
    /**
     * Constructor for the adapter.
     *
     * @param context   Application context.
     * @param itemsList List of Item objects to display.
     * @param listener  Listener for handling item selection.
     */
    public RecyclerGridViewAdapter(Context context, List<Item> itemsList, OnItemSelectListener listener) {
        this.context = context;
        this.itemsList = itemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_grid_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder
        Item item = itemsList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemQuantity.setText("Q: " + item.getQuantity());
        holder.itemLocation.setText(item.getLocation());

        if(item.getAlertLevel() > item.getQuantity()){// TODO:: I do not like this color I need to learn more about color and theme
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.red_700, null));
        }else{//TODO:: setting transparent is incorrect
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent, null));
        }

        // Increment item quantity on button click TODO:: Not efficient to make a call every time its clicked
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
        /**
         * Called when an item is selected.
         *
         * @param item The selected item.
         */
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
        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view representing a single item.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemLocation = itemView.findViewById(R.id.item_location);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
        }
    }
    /**
     * Updates the dataset and refreshes the RecyclerView.
     *
     * @param newItemsList The new list of items to display.
     */
    public void updateData(List<Item> newItemsList) {
        this.itemsList.clear();
        this.itemsList.addAll(newItemsList);
        notifyDataSetChanged();
    }
}
