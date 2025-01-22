package com.CS360.stocksense;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.CS360.stocksense.Database.AppDatabase;
import com.CS360.stocksense.Database.User;
import com.CS360.stocksense.Database.StarterData;
import com.CS360.stocksense.Database.UserDao;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private Button registerButton;
    private AppDatabase db;
    private static final String PREFERENCES_FILE = "com.CS360.stocksense.PREFERENCES_FILE";
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";
    private static final String KEY_REMEMBER_ME = "KEY_REMEMBER_ME";
    private static final String KEY_FIRST_LOGIN = "KEY_FIRST_LOGIN";
    private User currentUser;
    private boolean rationaleShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getInstance(this);

        StarterData.populateInitialData(this); // Populate initial data

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> onLoginButtonClick());
        registerButton.setOnClickListener(v -> onRegisterButtonClick());

        checkRememberedLogin();
    }

    private void onLoginButtonClick() {
        String username = usernameEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();
        boolean rememberMe = rememberMeCheckBox.isChecked();
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Username and password must be filled");
            return;
        }
        validateUser(username, password, rememberMe);
    }

    private void validateUser(String username, String password, boolean rememberMe) {
        new Thread(() -> {
            User user = db.userDao().getUser(username, password);
            currentUser = user;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (user != null) {
                    if (rememberMe) {
                        saveUserToPreferences(username, password);
                    } else {
                        clearUserPreferences();
                    }

                    SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
                    boolean isFirstLogin = preferences.getBoolean(KEY_FIRST_LOGIN, true);

                    if (isFirstLogin) {
                        requestSmsPermission();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(KEY_FIRST_LOGIN, true);
                        editor.apply();
                    } else {
                        navigateToInventoryGridView();
                    }
                } else {
                    showToast("Invalid username or password");
                }
            });
        }).start();
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) && !rationaleShown) {
                showSmsPermissionRationale();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {
            navigateToInventoryGridView();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("SMS Permission Granted");
                showPhoneNumberDialog(currentUser);
            } else {
                showToast("SMS Permission Denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) && !rationaleShown) {
                    showSmsPermissionRationale();
                } else {
                    navigateToInventoryGridView();
                }
            }
        }
    }

    private void showPhoneNumberDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Phone Number");
        builder.setMessage("The phone number is how we alert for low inventory levels.");

        final EditText input = new EditText(this);
        input.setHint("Phone Number");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String phoneNumber = input.getText().toString();
            user.setPhoneNumber(phoneNumber);
            user.setEnrolledInSMS(true);
            updateUser(user);
            navigateToInventoryGridView();
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void showSmsPermissionRationale() {
        rationaleShown = true;
        new AlertDialog.Builder(this)
                .setTitle("SMS Permission Needed")
                .setMessage("This app needs SMS permission to send notifications about low inventory levels.")
                .setPositiveButton("OK", (dialog, which) -> requestSmsPermission())
                .setNegativeButton("Cancel", (dialog, which) -> navigateToInventoryGridView())
                .create()
                .show();
    }

    private void navigateToInventoryGridView() {
        Intent intent = new Intent(LoginActivity.this, InventoryGridViewActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUserToPreferences(String username, String password) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.apply();
    }

    private void clearUserPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_REMEMBER_ME);
        editor.apply();
    }

    private void checkRememberedLogin() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        boolean rememberMe = preferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            String savedUsername = preferences.getString(KEY_USERNAME, null);
            String savedPassword = preferences.getString(KEY_PASSWORD, null);
            if (savedUsername != null && savedPassword != null) {
                usernameEditText.setText(savedUsername);
                passwordEditText.setText(savedPassword);
                rememberMeCheckBox.setChecked(true);
            }
        }
    }

    private void onRegisterButtonClick() {
        String username = usernameEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Username and password must be filled");
            return;
        }
        registerUser(username, password);
    }

    private void registerUser(String username, String password) {
        new Thread(() -> {
            UserDao userDao = db.userDao();
            User user = new User(username, password, "User", "0-000-000-000", false); // Default role is "User"
            userDao.insert(user);
            new Handler(Looper.getMainLooper()).post(() -> showToast("Registration successful!"));
        }).start();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void updateUser(User user) {
        new Thread(() -> db.userDao().updateUser(user)).start();
    }
}
