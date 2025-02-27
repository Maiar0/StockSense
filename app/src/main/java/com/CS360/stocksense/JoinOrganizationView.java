package com.CS360.stocksense;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.database.DataManager;

/**
 * JoinOrganizationView handles the process of joining or creating an organization.
 * Users can either input an existing organization UUID to join or enter a new
 * organization name to create one.
 *
 * <p>
 * Features:
 * - Allows users to input and join an organization using a UUID.
 * - Provides functionality for creating a new organization.
 * - Updates the user's organization information and logs them out for changes to take effect.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class JoinOrganizationView extends AppCompatActivity {
    private EditText joinOrganizationInput, organizationNameInput;
    private TextView currentOrganization;
    private DataManager dataManager;
    private SupabaseRepository repository;

    /**
     * Initializes the activity, UI components, and data managers.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_organization);

        joinOrganizationInput = findViewById(R.id.etOrganizationUUID);
        Button joinButton = findViewById(R.id.btnJoinOrganization);
        organizationNameInput = findViewById(R.id.etOrganizationName);
        Button createButton = findViewById(R.id.btnCreateOrganization);
        currentOrganization = findViewById(R.id.currentOrganization);
        joinButton.setOnClickListener(v -> joinOrganization());
        createButton.setOnClickListener(v -> createOrganization());
        dataManager = DataManager.getInstance(this);
        repository = new SupabaseRepository(this);

        currentOrganization.setText("Current Organization: " + repository.getOrganization());
        currentOrganization.setOnClickListener(v -> copyToClipboard(this, repository.getOrganization()));
    }

    /**
     * Handles joining an organization by updating organization details and logging the user out.
     */
    private void joinOrganization() {
        showPermanentActionDialog(this, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                dataManager.updateOrganization(joinOrganizationInput.getText().toString());
                repository.logout();
                finish();
            }

            @Override
            public void onError(Exception e) {
                // Handle error case (can be expanded as needed)
            }
        });

    }

    /**
     * Handles creating a new organization and updates the organization details on success.
     */
    private void createOrganization() {
        showPermanentActionDialog(this, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                dataManager.createOrganization(organizationNameInput.getText().toString(), new DataCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        dataManager.updateOrganization(result);
                        repository.logout();
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle error case (can be expanded as needed)
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                // Handle error case (can be expanded as needed)
            }
        });

    }

    public void showPermanentActionDialog(Context context, DataCallback<Boolean> callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Warning")
                .setMessage("Are you sure? This is a permanent action.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    Log.d("Dialog", "User confirmed the action.");
                    callback.onSuccess(true);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    callback.onSuccess(false);
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    private void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Database ID", text);
        clipboard.setPrimaryClip(clip);
    }
}
