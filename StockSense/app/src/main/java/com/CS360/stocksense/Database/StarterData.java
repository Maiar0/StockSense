package com.CS360.stocksense.Database;

import android.content.Context;

public class StarterData {

    // Initial users data
    private static final User[] INITIAL_USERS = {
            new User("admin", "admin123", "Admin", "1234567890", true),
            new User("user1", "password1", "User", "0987654321", false),
            new User("user2", "password2", "User", "1122334455", true)
    };

    // Initial items data
    private static final Items[] INITIAL_ITEMS = {
            new Items(15963,"Drills", 12, "A1", 5),
            new Items(49756,"Glue", 32, "B2", 10),
            new Items(8569,"Hammers", 12, "C3", 15),
            new Items(78945,"Nails", 250, "D4", 50),
            new Items(45648,"Screws", 250, "E5", 50),
            new Items(456132,"Tape", 25, "F6", 10),
            new Items(4562,"Tape", 25, "F6", 10),
            new Items(452,"Purple Paper", 25, "F6", 10),
            new Items(42,"Red Paper", 25, "F6", 10)
    };

    // Populate initial data into the database
    public static void populateInitialData(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        new Thread(() -> {
            UserDao userDao = db.userDao();
            ItemsDao itemsDao = db.itemsDao();

            if (userDao.getAllUsers().isEmpty()) { // Insert initial users if database is empty
                for (User user : INITIAL_USERS) {
                    userDao.insert(user);
                }
            }

            if (itemsDao.getAllItems().isEmpty()) { // Insert initial items if database is empty
                for (Items item : INITIAL_ITEMS) {
                    itemsDao.insert(item);
                }
            }
        }).start();
    }
}
