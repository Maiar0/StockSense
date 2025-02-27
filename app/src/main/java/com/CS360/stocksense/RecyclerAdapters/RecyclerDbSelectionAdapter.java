package com.CS360.stocksense.RecyclerAdapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CS360.stocksense.R;
import com.CS360.stocksense.models.DatabaseSelection;

import java.util.List;
/**
 * RecyclerDbSelectionAdapter is an adapter for displaying a list of databases in a RecyclerView.
 * Each item in the list represents a database, showing its name and ID.
 * Handles click events through the OnDatabaseClickListener interface.
 *
 * <p>
 * Features:
 * - Displays database name and ID.
 * - Provides a button to copy the database ID to the clipboard.
 * - Notifies listeners when a database is selected.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RecyclerDbSelectionAdapter extends RecyclerView.Adapter<RecyclerDbSelectionAdapter.ViewHolder> {

    private List<DatabaseSelection> databases;
    private final OnDatabaseClickListener clickListener;
    /**
     * Constructor for the adapter.
     *
     * @param databases     List of DatabaseSelection objects to display.
     * @param clickListener Listener for handling click events on database items.
     */
    public RecyclerDbSelectionAdapter(List<DatabaseSelection> databases, OnDatabaseClickListener clickListener) {
        this.databases = databases;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate and return a new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.db_selection_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder
        DatabaseSelection database = databases.get(position);
        holder.databaseName.setText(database.getName());
        holder.databaseId.setText(database.getId());
        holder.itemView.setOnClickListener(v -> clickListener.onDatabaseClick(database));
        holder.copyButton.setOnClickListener(v -> copyToClipboard(holder.itemView.getContext(), database.getId()));
    }
    /**
     * Copies the database ID to the clipboard.
     *
     * @param context The application context.
     * @param text    The text to copy.
     */
    private void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Database ID", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public int getItemCount() {
        // Return total number of items
        return databases.size();
    }
    /**
     * ViewHolder for individual database items in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView databaseName;
        TextView databaseId;
        Button copyButton;
        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view representing a single database item.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            databaseName = itemView.findViewById(R.id.database_name);
            databaseId = itemView.findViewById(R.id.database_id);
            copyButton = itemView.findViewById(R.id.copy_button);
        }
    }

    /**
     * Interface for handling click events on database items.
     */
    public interface OnDatabaseClickListener {
        /**
         * Called when a database is selected.
         *
         * @param database The selected database.
         */
        void onDatabaseClick(DatabaseSelection database);
    }
    /**
     * Updates the dataset and refreshes the RecyclerView.
     *
     * @param newDatabases The new list of items to display.
     */
    public void updateData(List<DatabaseSelection> newDatabases) {
        this.databases.clear();
        this.databases.addAll(newDatabases);
        notifyDataSetChanged();
    }
}
