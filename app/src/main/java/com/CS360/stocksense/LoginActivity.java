/**
 * LoginViewActivity handles the login screen of the StockSense app.
 * It manages user input for organization details and navigates to the
 * next activity upon successful login and network verification.
 */
package com.CS360.stocksense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.CS360.stocksense.auth.DataCallback;
import com.CS360.stocksense.database.DataManager;
import com.CS360.stocksense.auth.SupabaseRepository;
import com.CS360.stocksense.models.Item;

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
public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    private static final String KEY_EMAIL = "KEY_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> handleLoginClick());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        });


        Log.d("OnInstantiate", "LoginView ");
        populateSavedCredentials();

    }

    private void handleLoginClick() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("All fields are required.");
            return;
        }

        SupabaseRepository auth = new SupabaseRepository(this);
        auth.loginUser(email, password, new DataCallback<String>() {
            @Override
            public void onSuccess(String accessToken) {
                showToast("Login successful!");
                saveCredentials(email, accessToken);
                Log.d("LoginActivityStuff", "SavedCreds");
                navigateToNextActivity();
            }

            @Override
            public void onError(Exception e) {
                showToast("Login failed: " + e.getMessage());
            }
        });
    }

    private void saveCredentials(String email, String accessToken) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString("ACCESS_TOKEN", accessToken); // Store access token
        editor.apply();
    }
    /**
     * Loads saved credentials (if available).
     */
    private void populateSavedCredentials() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String savedEmail = preferences.getString(KEY_EMAIL, null);
        if (savedEmail != null) emailInput.setText(savedEmail);
    }
    /**
     * Navigates to the database selection screen and finishes the current activity.
     */
    private void navigateToNextActivity() {
        Log.d("LoginActivityStuff", "Navigate to next");
        // Initialize DataManager and clear data
        DataManager dataManager = DataManager.getInstance(LoginActivity.this);
        dataManager.clearCache();
        dataManager.fetchOrganizationItems(new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
                String organizationId = preferences.getString("OrganizationId", null);
                if(organizationId.equals("00000000-0000-0000-0000-000000000000")){
                    Intent intent = new Intent(LoginActivity.this, JoinOrganization.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(LoginActivity.this, DbSelectionViewActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onError(Exception e) {
                showToast("Error fetching data: " + e.getMessage());
            }
        });
        // Navigate
        //Intent intent = new Intent(LoginActivity.this, DbSelectionViewActivity.class);
        //startActivity(intent);
    }



    /**
     * Displays a toast message on the screen.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}