package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.auth.SupabaseRepository;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private SupabaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);

        repository = new SupabaseRepository(this);

        registerButton.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match.");
            return;
        }

        repository.registerUser(email, password, new DataCallback<String>() {
            @Override
            public void onSuccess(String message) {
                if(repository.getOrganization().equals("00000000-0000-0000-0000-000000000000") ){
                    Intent intent = new Intent(RegisterUserActivity.this, JoinOrganization.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onError(Exception e) {
                showToast("Registration failed: " + e.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
