package com.CS360.stocksense;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CS360.stocksense.models.Item;

import java.util.List;

public class RecyclerTableViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0; // View type for header
    private static final int VIEW_TYPE_ITEM = 1; // View type for items
    private List<Item> itemsList; // List of items to display
    private OnDeleteClickListener onDeleteClickListener; // Listener for delete button click

    public interface OnDeleteClickListener {
        void onDeleteClick(Item item); // Interface for delete click listener
    }

    public RecyclerTableViewAdapter(List<Item> itemsList, OnDeleteClickListener onDeleteClickListener) {
        this.itemsList = itemsList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM; // Determine view type based on position
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            // Inflate header layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_database_view_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Inflate item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_database_view, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            // Do nothing for header
        } else {
            // Bind data to item view
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Item item = itemsList.get(position - 1); // Adjust for header
            itemHolder.itemId.setText(String.valueOf(item.getId()));
            itemHolder.itemName.setText(item.getItemName());
            itemHolder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            itemHolder.itemLocation.setText(item.getLocation());

            itemHolder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(item)); // Set delete button click listener

            itemHolder.itemView.setOnClickListener(v -> {
                // Launch ItemDetailsActivity with item details
                Intent intent = new Intent(holder.itemView.getContext(), ItemDetailsActivity.class);
                intent.putExtra("item_id", item.getId());
                intent.putExtra("source_activity", "ListView");
                holder.itemView.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size() + 1; // Adjust for header
    }

    public void removeItem(Item item) {
        int position = itemsList.indexOf(item); // Find item position
        if (position != -1) {
            itemsList.remove(position); // Remove item from list
            notifyItemRemoved(position + 1); // Adjust for header and notify adapter
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView); // ViewHolder for header
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemId, itemName, itemQuantity, itemLocation; // Item details
        Button deleteButton; // Button to delete item

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemId = itemView.findViewById(R.id.item_id);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemLocation = itemView.findViewById(R.id.item_location);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
