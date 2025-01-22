package com.CS360.stocksense.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class, Items.class}, version = 1) // Define database with entities and version
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance; // Singleton instance

    public abstract UserDao userDao(); // UserDao access
    public abstract ItemsDao itemsDao(); // ItemsDao access

    // Get the database instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "stock_sense_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
