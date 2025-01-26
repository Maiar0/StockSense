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
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginViewActivity extends AppCompatActivity {

    private EditText organizationNameInput;
    private Button startLoginButton;
    private static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    private static final String KEY_ORGANIZATION = "KEY_ORGANIZATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        organizationNameInput = findViewById(R.id.organization_name);
        startLoginButton = findViewById(R.id.start_button);

        startLoginButton.setOnClickListener(v -> handleLoginClick());



        Log.d("OnInstantiate", "LoginView ");
        populateOrganizationFieldIfSaved();

    }
    /**
     * Handles the click event for the login button.
     * Validates organization input, checks network connectivity, and navigates to the next activity.
     */
    private void handleLoginClick() {
        String organization = organizationNameInput.getText().toString();

        if (organization.isEmpty()) {
            showToast(getString(R.string.invalid_login));
            return;
        }

        saveOrganizationToPreferences(organization);

        if(!isNetworkAvailable()){
            showToast(getString(R.string.no_network));
            return;
        }

        navigateToNextActivity();
    }
    /**
     * Saves the organization name to SharedPreferences for future use.
     *
     * @param organization The name of the organization to save.
     */
    private void saveOrganizationToPreferences(String organization) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ORGANIZATION, organization);
        editor.apply();
    }
    /**
     * Checks if a saved organization name exists in SharedPreferences and populates the input field.
     */
    private void populateOrganizationFieldIfSaved() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            String savedOrg = preferences.getString(KEY_ORGANIZATION, null);
            if (savedOrg != null ) {
                organizationNameInput.setText(savedOrg);
            }
    }
    /**
     * Navigates to the database selection screen and finishes the current activity.
     */
    private void navigateToNextActivity() {
        Intent intent = new Intent(LoginViewActivity.this, DbSelectionViewActivity.class); // Replace with your target activity
        startActivity(intent);
        finish();
    }
    /**
     * Checks if network connectivity is available.
     *
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