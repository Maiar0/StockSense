package com.CS360.stocksense;

import android.content.Context;
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

public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {

    private List<Item> itemsList; // List of items to display
    private int itemid;
    private Context context; // Context for launching activities

    public RecyclerGridViewAdapter(List<Item> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_grid_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder
        Item item = itemsList.get(position);
        itemid = item.getId();
        holder.itemName.setText(item.getItemName());
        holder.itemQuantity.setText("Q: " + item.getQuantity());
        holder.itemLocation.setText(item.getLocation());

        holder.incrementButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1); // Increment item quantity
            notifyItemChanged(position); // Notify adapter about item change
        });

        holder.decrementButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() - 1); // Decrement item quantity
            notifyItemChanged(position); // Notify adapter about item change
        });

        holder.itemView.setOnClickListener(v -> {
            // Launch ItemDetailsActivity with the item details
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("item_id", item.getId());
            intent.putExtra("source_activity", "GridView");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size(); // Return the total number of items
    }

    public void updateData(List<Item> newItemsList) {
        // Update the data and notify adapter about data change
        this.itemsList = newItemsList;
        notifyDataSetChanged();
    }

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

    public List<Item> getItemsList() {
        return itemsList; // Return the list of items
    }
}
