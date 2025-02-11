# StockSense

StockSense is an inventory management application designed to streamline the process of tracking stock levels, managing low inventory alerts, and providing a seamless user experience with an intuitive UI.

---

## Features

- **User Authentication**: Secure login with username and password.
- **Inventory Management**: Track and update stock levels efficiently.
- **Low Inventory Alerts**: Automatic SMS notifications for low stock items.
- **Recycler Views**: Displays inventory items in both list and grid formats.
- **Database Integration**: Uses Room database for persistent storage.

---

## Application Overview

### 1. **Login System**

The `LoginActivity` manages user authentication, allowing users to log in or register. Key features include:

- **SharedPreferences Integration**: Stores login details for quick access.
- **SMS Permission Handling**: Requests SMS permission for low inventory alerts.
- **User Validation**: Checks credentials against the Room database.

> **Related File:** [`LoginActivity.java`](app/src/main/java/com/CS360/stocksense/LoginActivity.java)

### 2. **Main Activity and Navigation**

The `MainActivity` serves as the central hub, allowing navigation to different inventory views:

- **Inventory Grid View** (`InventoryGridViewActivity`)
- **Database List View** (`DatabaseViewActivity`)
- **Item Creation Dialog**: Adds new inventory items to the database.

Additionally, the app sets up a background worker (`LowInventoryWorker`) to monitor stock levels and send SMS alerts.

> **Related Files:**  
> - [`MainActivity.java`](app/src/main/java/com/CS360/stocksense/MainActivity.java)  
> - [`LowInventoryWorker.java`](app/src/main/java/com/CS360/stocksense/LowInventoryWorker.java)

### 3. **Inventory Views**

#### **Database View (`DatabaseViewActivity`)**
Displays inventory items in a **list format** using `RecyclerListViewAdapter`. Key functionalities include:

- Fetching items from the database (`AppDatabase`).
- Providing an option to delete items via an alert dialog.

> **Related Files:**  
> - [`DatabaseViewActivity.java`](app/src/main/java/com/CS360/stocksense/DatabaseViewActivity.java)  
> - [`RecyclerListViewAdapter.java`](app/src/main/java/com/CS360/stocksense/RecyclerListViewAdapter.java)

#### **Inventory Grid View (`InventoryGridViewActivity`)**
Displays inventory items in a **grid format** using `RecyclerGridViewAdapter`. Key functionalities include:

- Fetching and sorting items by name.
- Updating inventory items when changes are made.
- Allows quick updates to stock levels via increment/decrement buttons.

> **Related Files:**  
> - [`InventoryGridViewActivity.java`](app/src/main/java/com/CS360/stocksense/InventoryGridViewActivity.java)  
> - [`RecyclerGridViewAdapter.java`](app/src/main/java/com/CS360/stocksense/RecyclerGridViewAdapter.java)

### 4. **Item Details and Editing**

The `ItemDetailsActivity` provides a **detailed view of individual items**, allowing users to:

- Modify quantity, location, and alert level.
- Save changes to the database.
- Delete an item with a confirmation dialog.

> **Related File:** [`ItemDetailsActivity.java`](app/src/main/java/com/CS360/stocksense/ItemDetailsActivity.java)

---

## Database Backend

StockSense uses **Room Database**, an abstraction layer over SQLite, to manage inventory and user data. The database consists of two main tables:

1. **Users Table** (`User`): Stores user credentials, roles, and phone numbers for SMS notifications.
2. **Items Table** (`Items`): Stores inventory items, their quantities, locations, and alert levels.

---

### 1. **Database Configuration**
The database is defined as a **singleton instance** using Room’s `RoomDatabase` class. This ensures a single point of access across the app.

```java
@Database(entities = {User.class, Items.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ItemsDao itemsDao();

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
```
> **Related File:** [`AppDatabase.java`](app/src/main/java/com/CS360/stocksense/Database/AppDatabase.java)

---

### 2. **User Table**
The `User` entity stores **authentication and SMS notification preferences**.

| Column         | Type     | Description                               |
|---------------|---------|-------------------------------------------|
| `id`          | int (PK) | Auto-generated primary key               |
| `username`    | String   | Unique username                          |
| `password`    | String   | User password                            |
| `role`        | String   | Role (`Admin` or `User`)                 |
| `phoneNumber` | String   | Contact number for SMS alerts            |
| `isEnrolledInSMS` | boolean | Whether the user is subscribed to alerts |

```java
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private String role;
    private String phoneNumber;
    private boolean isEnrolledInSMS;
}
```
> **Related File:** [`User.java`](app/src/main/java/com/CS360/stocksense/Database/User.java)

#### **UserDao**
The `UserDao` provides access methods for retrieving and updating user records.

```java
@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User getUser(String username, String password);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Update
    void updateUser(User user);
}
```
> **Related File:** [`UserDao.java`](app/src/main/java/com/CS360/stocksense/Database/UserDao.java)

---

### 3. **Items Table**
The `Items` entity stores **inventory data**.

| Column              | Type     | Description                               |
|--------------------|---------|-------------------------------------------|
| `id`              | int (PK) | Unique item identifier                    |
| `itemName`        | String   | Name of the item                          |
| `quantity`        | int      | Current stock level                       |
| `location`        | String   | Location of the item                      |
| `alertLevel`      | int      | Threshold for low stock alerts            |
| `lastAlertTimestamp` | long  | Timestamp of last low-stock alert         |

```java
@Entity(tableName = "items")
public class Items {
    @PrimaryKey
    private int id;
    private String itemName;
    private int quantity;
    private String location;
    private int alertLevel;
    private long lastAlertTimestamp;
}
```
> **Related File:** [`Items.java`](app/src/main/java/com/CS360/stocksense/Database/Items.java)

#### **ItemsDao**
The `ItemsDao` provides methods for **CRUD operations** and checking low inventory alerts.

```java
@Dao
public interface ItemsDao {
    @Insert
    void insert(Items item);

    @Update
    void update(Items item);

    @Delete
    void delete(Items item);

    @Query("SELECT * FROM items")
    List<Items> getAllItems();

    @Query("SELECT * FROM items WHERE id = :id")
    Items getItemById(int id);

    @Query("SELECT (quantity < alertLevel) FROM items WHERE id = :itemId")
    boolean isLowInventory(int itemId);
}
```
> **Related File:** [`ItemsDao.java`](app/src/main/java/com/CS360/stocksense/Database/ItemsDao.java)

---

### 4. **Starter Data**
To ensure a pre-filled database, StockSense initializes **starter data** on first launch.

#### **Initial Users**
| Username | Password  | Role  | Phone Number | SMS Alerts |
|----------|----------|-------|--------------|------------|
| admin    | admin123 | Admin | 1234567890   | ✅         |
| user1    | password1 | User | 0987654321   | ❌         |
| user2    | password2 | User | 1122334455   | ✅         |

#### **Initial Inventory**
| ID     | Item Name   | Quantity | Location | Alert Level |
|--------|------------|----------|----------|-------------|
| 15963  | Drills     | 12       | A1       | 5           |
| 49756  | Glue       | 32       | B2       | 10          |
| 8569   | Hammers    | 12       | C3       | 15          |
| 78945  | Nails      | 250      | D4       | 50          |
| 45648  | Screws     | 250      | E5       | 50          |

```java
public static void populateInitialData(Context context) {
    AppDatabase db = AppDatabase.getInstance(context);
    new Thread(() -> {
        UserDao userDao = db.userDao();
        ItemsDao itemsDao = db.itemsDao();

        if (userDao.getAllUsers().isEmpty()) {
            for (User user : INITIAL_USERS) {
                userDao.insert(user);
            }
        }

        if (itemsDao.getAllItems().isEmpty()) {
            for (Items item : INITIAL_ITEMS) {
                itemsDao.insert(item);
            }
        }
    }).start();
}
```
> **Related File:** [`StarterData.java`](app/src/main/java/com/CS360/stocksense/Database/StarterData.java)

---

## **Limitations**

While StockSense provides a functional inventory management system, it has several **limitations** that should be considered for future improvements.

### **1. Insecure Password Storage**
- User passwords are stored **in plaintext** within the database.
- There is **no password hashing or encryption**, making the application vulnerable to **database leaks** or unauthorized access.

  **Potential Fix:** Implement secure password hashing using **BCrypt** or **Argon2** before storing credentials.

### **2. Single Database Instance (No Remote Access)**
- The app uses a **local Room database**, meaning:
  - There is **no cloud storage** or remote access.
  - Users **must access inventory from a single device**.

  **Potential Fix:** Use **Firebase Firestore, Supabase, or a remote MySQL/PostgreSQL server** to enable multi-device synchronization.

### **3. Single User System**
- **No multi-user support**—the application only allows **one active user per installation**.
- **No user roles or permissions**, meaning:
  - Admins and users have the same level of access.
  - Any logged-in user can modify inventory data.

  **Potential Fix:** Implement **role-based access control (RBAC)** and support multiple users with **unique sessions**.

### **4. No Network Sync or API Integration**
- The app functions entirely **offline** with no API support.
- Inventory updates cannot be synced across multiple devices.

  **Potential Fix:** Introduce **REST API support** for **real-time updates** and **cloud storage**.

### **5. No Data Backup or Export**
- **If the database is lost or corrupted, all inventory data is gone.**
- Users cannot **export** inventory reports.

  **Potential Fix:** Implement **automatic database backup**, **export to CSV/PDF**, or **cloud sync**.

### **6. No Input Validation for User Data**
- There are **no constraints** preventing:
  - **Weak passwords** (e.g., `"1234"`, `"password"`).
  - **Duplicate usernames**.
  - **Invalid phone numbers** for SMS alerts.

  **Potential Fix:** Implement **input validation** and **enforce password complexity**.

### **7. Limited SMS Alert System**
- The app **relies on SMS notifications**, which:
  - Require **SMS permission approval** from the user.
  - **Might not work** on devices without an SMS plan.

  **Potential Fix:** Introduce **push notifications** or **email alerts** as alternatives.



