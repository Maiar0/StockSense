/**
 * LoginViewActivity handles the login screen of the StockSense app.
 * It manages user input for organization details and navigates to the
 * next activity upon successful login and network verification.
 */
package com.CS360.stocksense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.CS360.stocksense.Supabase.DataCallback;
import com.CS360.stocksense.Supabase.SupabaseRepository;
import com.CS360.stocksense.models.Organization;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 * LoginViewActivity
 *
 * This activity represents the login screen of the StockSense application. It handles
 * user input for organization details, validates the input, checks for network connectivity,
 * and navigates to the next activity upon successful login.
 *
 * Key Features:
 * - Allows users to enter their organization name.
 * - Saves and retrieves the organization name using SharedPreferences for convenience.
 * - Validates user input and ensures a valid network connection before proceeding.
 * - Navigates to the `DbSelectionViewActivity` upon successful login.
 *
 * Responsibilities:
 * - Manages UI interactions for the login screen.
 * - Validates user input for the organization name.
 * - Ensures network availability before progressing further.
 * - Provides user feedback through toast messages for errors such as invalid input or network issues.
 *
 * Notes:
 * - The activity relies on SharedPreferences to store and retrieve the organization name.
 * - Network connectivity is checked using the `ConnectivityManager` system service.
 * - Future extensions could include user authentication or additional input validation.
 *
 * Example Usage:
 * - The application launches this activity as the entry point for users to log in to their organization.
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class LoginViewActivity extends AppCompatActivity {

    private EditText organizationNameInput, passwordInput;
    private Button loginButton, registerButton;
    private static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    private static final String KEY_ORGANIZATION = "KEY_ORGANIZATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        organizationNameInput = findViewById(R.id.organization_name);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> handleLoginClick());
        registerButton.setOnClickListener(v-> showRegisterDialog());

        Log.d("OnInstantiate", "LoginView ");
        populateSavedCredentials();

    }
    /**
     * Handles the click event for the login button.
     * Validates organization input, checks network connectivity, and navigates to the next activity.
     */
    private void handleLoginClick() {
        String organization = organizationNameInput.getText().toString();
        String password = passwordInput.getText().toString().trim();

        if (organization.isEmpty() || password.isEmpty()) {
            showToast("All fields are required.");
            return;
        }
        // Hash the password using SHA-256
        String hashedPassword = hashPassword(password);
        // Validate login credentials via API
        SupabaseRepository repository = new SupabaseRepository();
        repository.validateUser(organization, hashedPassword, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                showToast("Login successful!");
                saveCredentials(organization);
                navigateToNextActivity();
            }

            @Override
            public void onError(Exception e) {
                showToast("Login failed: " + e.getMessage());
            }
        });
        saveCredentials(organization);
    }
    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register New User");

        // Layout for input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Username input
        EditText emailInput = new EditText(this);
        emailInput.setHint("Enter E-mail");
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(emailInput);

        // Username input
        EditText organizationInput = new EditText(this);
        organizationInput.setHint("Enter Organization");
        organizationInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(organizationInput);

        // Password input
        EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter Password");
        passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);

        builder.setView(layout);

        // Set dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            String organization = organizationInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (organization.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showToast("All fields are required.");
                return;
            }

            // Hash password using SHA-256
            String hashedPassword = hashPassword(password);

            // Create User object
            Organization newOrganization = new Organization(organization, hashedPassword, email);
            registerUser(newOrganization);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }
    private void registerUser(Organization organization) {
        SupabaseRepository repository = new SupabaseRepository();

        repository.registerUser(organization, new DataCallback<Organization>() {
            @Override
            public void onSuccess(Organization result) {
                showToast("User registered successfully!");
                Log.d("RegisterUser", "User registered: " + result);
            }

            @Override
            public void onError(Exception e) {
                showToast("Registration failed: " + e.getMessage());
                Log.e("RegisterUser", "Error registering user", e);
            }
        });
    }

    /**
     * Saves organization, username
     */
    private void saveCredentials(String organization) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ORGANIZATION, organization);
        editor.apply();
    }

    /**
     * Loads saved credentials (if available).
     */
    private void populateSavedCredentials() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String savedOrg = preferences.getString(KEY_ORGANIZATION, null);
        if (savedOrg != null) organizationNameInput.setText(savedOrg);
    }
    /**
     * Hashes a password using SHA-256.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("SHA-256", "Hashing failed", e);
            return password; // Not secure, but avoids crashes
        }
    }
    /**
     * Navigates to the database selection screen and finishes the current activity.
     */
    private void navigateToNextActivity() {
        Intent intent = new Intent(LoginViewActivity.this, DbSelectionViewActivity.class); // Replace with your target activity
        startActivity(intent);
    }
    /**
     * Checks if network connectivity is available.
     *TODO:: Decided if we need this
     * @return True if network is available, false otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }
    /**
     * Displays a toast message on the screen.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(LoginViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}