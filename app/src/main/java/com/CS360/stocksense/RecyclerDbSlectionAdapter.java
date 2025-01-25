package com.CS360.stocksense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CS360.stocksense.models.DatabaseSelection;
import com.CS360.stocksense.models.Item;

import java.util.List;
public class RecyclerDbSlectionAdapter extends RecyclerView.Adapter<RecyclerDbSlectionAdapter.ViewHolder> {

    private List<DatabaseSelection> databaseList;
    private OnDatabaseClickListener clickListener;

    public RecyclerDbSlectionAdapter(List<DatabaseSelection> databaseList, OnDatabaseClickListener clickListener) {
        this.databaseList = databaseList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual database items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.db_selection_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseSelection database = databaseList.get(position);

        // Set the database name and ID
        holder.databaseName.setText(database.getName());
        holder.databaseId.setText(database.getId());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> clickListener.onDatabaseClick(database));
    }

    @Override
    public int getItemCount() {
        return databaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView databaseName;
        TextView databaseId;

        public ViewHolder(View itemView) {
            super(itemView);
            databaseName = itemView.findViewById(R.id.database_name);
            databaseId = itemView.findViewById(R.id.database_id);
        }
    }

    public List<DatabaseSelection> getList() {
        return databaseList; // Return the list of databases
    }

    // Interface for handling click events
    public interface OnDatabaseClickListener {
        void onDatabaseClick(DatabaseSelection database);
    }
}
