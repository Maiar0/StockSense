package com.CS360.stocksense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CS360.stocksense.models.DatabaseSelection;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_db_selection_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the data to the ViewHolder
        DatabaseSelection database = databaseList.get(position);
        holder.databaseName.setText(database.getName());

        // Set click listener for item
        holder.itemView.setOnClickListener(v -> clickListener.onDatabaseClick(database));
    }

    @Override
    public int getItemCount() {
        return databaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView databaseName;

        public ViewHolder(View itemView) {
            super(itemView);
            databaseName = itemView.findViewById(R.id.database_name);
        }
    }

    // Interface for handling click events
    public interface OnDatabaseClickListener {
        void onDatabaseClick(DatabaseSelection database);
    }
}
