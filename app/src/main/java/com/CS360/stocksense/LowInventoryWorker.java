package com.CS360.stocksense;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.CS360.stocksense.Database.AppDatabase;
import com.CS360.stocksense.Database.Items;
import com.CS360.stocksense.Database.ItemsDao;
import com.CS360.stocksense.Database.User;
import com.CS360.stocksense.Database.UserDao;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LowInventoryWorker extends Worker {

    private static final long ALERT_INTERVAL = TimeUnit.HOURS.toMillis(24); // Alert interval of 24 hours

    public LowInventoryWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        ItemsDao itemsDao = db.itemsDao();

        List<Items> itemsList = itemsDao.getAllItems(); // Get all items from the database
        long currentTime = System.currentTimeMillis(); // Get current time

        for (Items item : itemsList) {
            if (item.isLowInventory()) { // Check if the item has low inventory
                long lastAlertTime = item.getLastAlertTimestamp();
                if (currentTime - lastAlertTime >= ALERT_INTERVAL) { // Check if it's time to send an alert
                    sendSmsAlert(item); // Send SMS alert
                    item.setLastAlertTimestamp(currentTime); // Update last alert timestamp
                    itemsDao.update(item); // Update item in the database
                }
            }
        }
        return Result.success();
    }

    private void sendSmsAlert(Items item) {
        String phoneNumber = getUserPhoneNumber(); // Get user's phone number
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String message = "Alert: Item " + item.getItemName() + " is low on inventory.";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null); // Send SMS message
        }
    }

    private String getUserPhoneNumber() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.CS360.stocksense.PREFERENCES_FILE", Context.MODE_PRIVATE);
        String savedUsername = preferences.getString("KEY_USERNAME", null);
        String savedPassword = preferences.getString("KEY_PASSWORD", null);

        if (savedUsername != null) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            UserDao userDao = db.userDao();
            User user = userDao.getUser(savedUsername, savedPassword); // Get user from the database
            if (user != null && user.isEnrolledInSMS()) {
                return user.getPhoneNumber(); // Return user's phone number if enrolled in SMS alerts
            }
        }
        return null;
    }
}
