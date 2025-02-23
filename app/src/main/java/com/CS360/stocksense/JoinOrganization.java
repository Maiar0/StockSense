package com.CS360.stocksense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.database.DataManager;

public class JoinOrganization extends AppCompatActivity {
    private EditText joinOrganizationInput, organizationNameInput;
    private Button joinButton, createButton;
    private DataManager dataManager;
    private SupabaseRepository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_organization);

        joinOrganizationInput = findViewById(R.id.etOrganizationUUID);
        joinButton = findViewById(R.id.btnJoinOrganization);
        organizationNameInput = findViewById(R.id.etOrganizationName);
        createButton = findViewById(R.id.btnCreateOrganization);

        joinButton.setOnClickListener(v -> joinOrganization());
        createButton.setOnClickListener(v -> createOrganization());
        dataManager = DataManager.getInstance(this);
        repository = new SupabaseRepository(this);
    }
    private void joinOrganization() {
        dataManager.updateOrganization(joinOrganizationInput.getText().toString());
        repository.logout();
        finish();
    }

    private void createOrganization() {
        // TODO:: implement creating Organization
    }


}