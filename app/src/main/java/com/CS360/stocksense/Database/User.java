package com.CS360.stocksense.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users") // Define users table
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String password;
    private String role;
    private String phoneNumber; // Phone number attribute
    private boolean isEnrolledInSMS; // SMS enrollment attribute

    // Constructor
    public User(String username, String password, String role, String phoneNumber, boolean isEnrolledInSMS) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.isEnrolledInSMS = isEnrolledInSMS;
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getter and setter for phone number
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getter and setter for SMS enrollment
    public boolean isEnrolledInSMS() {
        return isEnrolledInSMS;
    }

    public void setEnrolledInSMS(boolean enrolledInSMS) {
        isEnrolledInSMS = enrolledInSMS;
    }
}
