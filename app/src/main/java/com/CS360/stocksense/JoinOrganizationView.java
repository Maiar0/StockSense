package com.CS360.stocksense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.database.DataManager;

public class JoinOrganizationView extends AppCompatActivity {
    private EditText joinOrganizationInput, organizationNameInput;
    private DataManager dataManager;
    private SupabaseRepository repository;
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
    private void joinOrganization() {
        dataManager.updateOrganization(joinOrganizationInput.getText().toString());
        repository.logout();
        finish();
    }

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

            }
        });
    }


}