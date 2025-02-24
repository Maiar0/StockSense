package com.CS360.stocksense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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

        joinButton.setOnClickListener(v -> joinOrganization());
        createButton.setOnClickListener(v -> createOrganization());
        dataManager = DataManager.getInstance(this);
        repository = new SupabaseRepository(this);
    }

    /**
     * Handles joining an organization by updating organization details and logging the user out.
     */
    private void joinOrganization() {
        dataManager.updateOrganization(joinOrganizationInput.getText().toString());
        repository.logout();
        finish();
    }

    /**
     * Handles creating a new organization and updates the organization details on success.
     */
    private void createOrganization() {
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
}
