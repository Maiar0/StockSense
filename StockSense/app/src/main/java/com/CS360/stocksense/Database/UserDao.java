package com.CS360.stocksense.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao // Data Access Object for User
public interface UserDao {

    @Insert // Insert a new user
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1") // Get user by username and password
    User getUser(String username, String password);

    @Query("SELECT * FROM users") // Get all users
    List<User> getAllUsers();

    @Update // Update user information
    void updateUser(User user);
}
