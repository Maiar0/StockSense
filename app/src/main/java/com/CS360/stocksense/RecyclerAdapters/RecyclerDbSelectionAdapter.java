package com.CS360.stocksense.RecyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.R;
import com.CS360.stocksense.models.DatabaseSelection;

import java.util.List;
/**
 * RecyclerDbSelectionAdapter
 *
 * Adapter for displaying a list of databases in a RecyclerView. Each item in the list represents
 * a database, showing its name and ID. Handles click events through the OnDatabaseClickListener interface.
 *
 * Features:
 * - Inflates the layout for database items.
 * - Binds data (name and ID) to individual item views.
 * - Allows click handling for database selection.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RecyclerDbSelectionAdapter extends RecyclerView.Adapter<RecyclerDbSelectionAdapter.ViewHolder> {

    private List<DatabaseSelection> databases;
    private OnDatabaseClickListener clickListener;
    /**
     * Constructor for the adapter.
     *
     * @param databases List of DatabaseSelection objects to display.
     * @param clickListener      Listener for handling click events on database items.
     */
    public RecyclerDbSelectionAdapter(List<DatabaseSelection> databases, OnDatabaseClickListener clickListener) {
        this.databases = databases;
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
        DatabaseSelection database = databases.get(position);

        // Set the database name and ID
        holder.databaseName.setText(database.getName());
        holder.databaseId.setText(database.getId());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> clickListener.onDatabaseClick(database));
    }

    @Override
    public int getItemCount() {
        return databases.size();
    }
    /**
     * ViewHolder for individual database items in the RecyclerView.
     * Holds references to the database name and ID TextViews.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView databaseName;
        TextView databaseId;

        public ViewHolder(View itemView) {
            super(itemView);
            databaseName = itemView.findViewById(R.id.database_name);
            databaseId = itemView.findViewById(R.id.database_id);
        }
    }
    //TODO:: Decide if this will be removed.
    /**
     * Returns the current list of database selections.
     *
     * @return List of DatabaseSelection objects.
     */
    public List<DatabaseSelection> getDatabaseList() {
        return databases; // Return the list of databases
    }
    /**
     * Interface for handling click events on database items.
     */
    public interface OnDatabaseClickListener {
        void onDatabaseClick(DatabaseSelection database);
    }
}
