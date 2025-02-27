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
 * LoginView handles the login screen of the StockSense application.
 * It manages user authentication, input validation, and navigation to the next screen
 * based on successful login and organization status.
 *
 * <p>
 * Features:
 * - Allows users to enter email and password for authentication.
 * - Saves and retrieves email using SharedPreferences.
 * - Validates user input before making login requests.
 * - Navigates to the appropriate screen based on user authentication and organization status.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class LoginView extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    private static final String KEY_EMAIL = "KEY_EMAIL";

    /**
     * Initializes the activity, sets up UI components, and loads saved credentials.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> handleLoginClick());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginView.this, RegisterUserView.class);
            startActivity(intent);
        });

        Log.d("OnInstantiate", "LoginView ");
        populateSavedCredentials();
    }

    /**
     * Handles login button click event. Validates user input and initiates authentication.
     */
    private void handleLoginClick() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("All fields are required.");
            return;
        }

        SupabaseRepository repository = new SupabaseRepository(this);
        repository.loginUser(email, password, new DataCallback<String>() {
            @Override
            public void onSuccess(String accessToken) {
                showToast("Login successful!");
                saveCredentials(email);
                Log.d(this.getClass().getSimpleName(), "saveCredentials");
                navigateToNextActivity();
            }

            @Override
            public void onError(Exception e) {
                showToast("Login failed: " + e.getMessage());
            }
        });
    }

    /**
     * Saves user credentials in SharedPreferences.
     *
     * @param email       The user's email.
     */
    private void saveCredentials(String email) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * Loads saved credentials (if available) and populates the email input field.
     */
    private void populateSavedCredentials() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String savedEmail = preferences.getString(KEY_EMAIL, null);
        if (savedEmail != null) emailInput.setText(savedEmail);
    }

    /**
     * Navigates to the appropriate activity based on organization status.
     */
    private void navigateToNextActivity() {
        Log.d(this.getClass().getSimpleName(), "Navigate to next");
        // Initialize DataManager and clear cached data
        DataManager dataManager = DataManager.getInstance(LoginView.this);
        dataManager.clearCache();
        dataManager.fetchOrganizationItems(new DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
                String organizationId = preferences.getString("OrganizationId", null);
                Intent intent;
                if (organizationId != null && organizationId.equals("00000000-0000-0000-0000-000000000000")) {
                    intent = new Intent(LoginView.this, JoinOrganizationView.class);
                } else {
                    intent = new Intent(LoginView.this, MainView.class);
                }
                startActivity(intent);
            }

            @Override
            public void onError(Exception e) {
                showToast("Error fetching data: " + e.getMessage());
            }
        });
    }

    /**
     * Displays a toast message.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(LoginView.this, message, Toast.LENGTH_SHORT).show();
    }
}
