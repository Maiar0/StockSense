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

## Next Steps

In the next section, we will outline the **database backend**, including:

- Database schema (`AppDatabase`)
- Data Access Objects (`ItemsDao`, `UserDao`)
- Data models (`Items`, `User`)
- Starter data implementation (`StarterData`)

---
