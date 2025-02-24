package com.CS360.stocksense;

import android.content.Intent;
import android.os.Bundle;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.auth.SupabaseRepository;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * RegisterUserView handles user registration in the StockSense application.
 * It allows users to create an account, validate input, and navigate to the appropriate screen
 * based on organization status after successful registration.
 *
 * <p>
 * Features:
 * - Allows users to input email and password for account creation.
 * - Validates user input, including password confirmation.
 * - Handles user authentication with Supabase.
 * - Navigates to the next screen based on organization status.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class RegisterUserView extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private SupabaseRepository repository;

    /**
     * Initializes the activity, sets up UI components, and handles user input.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button registerButton = findViewById(R.id.registerButton);

        repository = new SupabaseRepository(this);

        registerButton.setOnClickListener(v -> handleRegister());
    }

    /**
     * Handles user registration by validating input and creating a new account.
     */
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
                    Intent intent = new Intent(RegisterUserView.this, JoinOrganizationView.class);
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

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
