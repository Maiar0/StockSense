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

public class LoginActivity extends AppCompatActivity {

    private EditText orgEditText;
    private Button startButton;
    private static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    private static final String KEY_ORGANIZATION = "KEY_ORGANIZATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        orgEditText = findViewById(R.id.organization_name);
        startButton = findViewById(R.id.start_button);

        startButton.setOnClickListener(v -> onStartClick());



        Log.d("OnInstantiate", "LoginView ");
        checkLogin();

    }

    private void onStartClick() {
        String organization = orgEditText.getText().toString();

        if (organization.isEmpty()) {
            showToast("Organization must be filled");
            return;
        }

        saveOrganization(organization);

        if(!isNetworkAvailable()){
            showToast("No internet connection!");
            return;
        }

        navigateToNextActivity();
    }

    private void saveOrganization(String organization) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ORGANIZATION, organization);
        editor.apply();
    }
    //TODO:: deprecate?
    private void clearPrefs() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_ORGANIZATION);
        editor.apply();
    }

    private void checkLogin() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            String savedOrg = preferences.getString(KEY_ORGANIZATION, null);
            if (savedOrg != null ) {
                orgEditText.setText(savedOrg);
            }
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, DbSelectionView.class); // Replace with your target activity
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // For Android 6.0 (API 23) and above
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                    return networkCapabilities != null &&
                            (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                }
            } else {
                // For older Android versions
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }
}