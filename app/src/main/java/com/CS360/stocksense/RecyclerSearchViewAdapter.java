package com.CS360.stocksense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CS360.stocksense.models.Item;
import java.util.ArrayList;
import java.util.List;

public class RecyclerSearchViewAdapter extends RecyclerView.Adapter<RecyclerSearchViewAdapter.ViewHolder> {

    private List<Item> itemsList;
    private List<Item> filteredItemsList;
    private OnItemSelectListener listener;

    // Updated constructor to accept an OnItemSelectListener
    public RecyclerSearchViewAdapter(List<Item> itemsList, OnItemSelectListener listener) {
        this.itemsList = itemsList;
        this.filteredItemsList = new ArrayList<>(itemsList); // Initialize with all items
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = filteredItemsList.get(position);
        holder.itemId.setText("ID: " + item.getItem_id());
        holder.itemName.setText(item.getItemName());
        holder.quantity.setText("Quantity: " + item.getQuantity());

        // Handle item selection
        holder.itemView.setOnClickListener(v -> listener.onItemSelected(item));
    }

    @Override
    public int getItemCount() {
        return filteredItemsList.size();
    }

    public void filter(String query) {
        filteredItemsList.clear();

        if (query.isEmpty()) {
            filteredItemsList.addAll(itemsList); // Show all items if query is empty
        } else {
            for (Item item : itemsList) {
                if (item.getItemName().toLowerCase().contains(query.toLowerCase())) {
                    filteredItemsList.add(item);
                }
            }
        }

        notifyDataSetChanged(); // Notify adapter to refresh RecyclerView
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemId, itemName, quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            itemId = itemView.findViewById(R.id.item_id);
            itemName = itemView.findViewById(R.id.item_name);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

    // Interface for item selection
    public interface OnItemSelectListener {
        void onItemSelected(Item item);
    }
}
