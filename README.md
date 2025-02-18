# Enhancement Three: API Integration

This enhancement focuses on **migrating from a local database to an online Supabase database**, enabling **multi-user access, real-time inventory management, and secure authentication**.

---

## **Overview of API Calls**
StockSense interacts with Supabase for:
- **User Authentication**
- **Fetching, Creating, Updating, and Deleting Inventory Data**
- **Database Selection and Management**

### **1. User Authentication**
> **File:** [`LoginRequest.java`](app/src/main/java/com/CS360/stocksense/models/LoginRequest.java)

- Users log in using their **organization name** and **hashed password**.
- Requests are sent via **POST** to the `/rpc/validate_user` endpoint.
- Implemented in `SupabaseRepository.validateUser()`.

```java
public void validateUser(String username, String hashedPassword, DataCallback<Boolean> callback) {
    LoginRequest loginRequest = new LoginRequest(username, hashedPassword);
    api.validateUser(apiKey, authToken, loginRequest).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                callback.onSuccess(true);
            } else {
                callback.onError(new Exception("Invalid username or password."));
            }
        }
    });
}
```

### **2. Fetching and Managing Inventory Data**
> **File:** [`DataManager.java`](app/src/main/java/com/CS360/stocksense/Supabase/DataManager.java)

- Items are retrieved from Supabase using **GET** requests.
- Supports **fetching all items in a database** or a **single item by ID**.

```java
public void fetchDatabase(String organizationName, String databaseId, DataCallback<List<Item>> callback) {
    repository.fetchDatabase(organizationName, databaseId, callback);
}

public void fetchSingleItem(String organizationName, String itemId, String databaseId, DataCallback<Item> callback) {
    repository.readItem(organizationName, itemId, databaseId, callback);
}
```

### **3. Creating, Updating, and Deleting Inventory Items**
> **File:** [`SupabaseRepository.java`](app/src/main/java/com/CS360/stocksense/Supabase/SupabaseRepository.java)

- **Create Items:** Uses `POST` to add items.
- **Update Items:** Uses `PATCH` to modify existing entries.
- **Delete Items:** Uses `DELETE` to remove items by `item_id`.

```java
public void createItems(String organizationName, List<Item> items, String databaseId, DataCallback<List<Item>> callback) {
    repository.createItem(organizationName, items, databaseId, callback);
}

public void updateItem(String organizationName, Item item, String databaseId, DataCallback<List<Item>> callback) {
    repository.updateItem(organizationName, item, databaseId, callback);
}

public void deleteItem(String organizationName, String itemId, String databaseId, DataCallback<Void> callback) {
    repository.deleteItem(organizationName, itemId, databaseId, callback);
}
```

### **4. Database Selection and Management**
> **File:** [`DatabaseSelection.java`](app/src/main/java/com/CS360/stocksense/models/DatabaseSelection.java)

- Users can select a **multi-user database** hosted on Supabase.
- Uses **GET requests** to fetch available databases.

```java
public void fetchOrganization(String organizationName, DataCallback<List<DatabaseSelection>> callback) {
    repository.fetchOrganization(organizationName, callback);
}

public void deleteDatabase(String databaseId, DataCallback<Void> callback) {
    repository.deleteDatabase(databaseId, callback);
}
```

# Enhancement Three: Supabase Database Structure

This section documents the **database structure** for StockSense after transitioning to **Supabase**, replacing the previous local Room database. The new structure enables **multi-user access**, **real-time inventory management**, and **secure authentication**.

---

## **Database Overview**
The Supabase database consists of two primary tables:

1. **User Table (`users`)** - Manages authentication and user details.
2. **Item Table (`items`)** - Stores inventory data, linked to organizations using `organization_name`.

---

## **1. User Table (`users`)**
This table is used to store login information.

| Column Name       | Type      | Description |
|------------------|----------|-------------|
| `id`            | UUID (PK) | Unique user ID. |
| `email`         | Text      | User email (used for login). |
| `password_hash` | Text      | Hashed password (automatically managed by Supabase). |
| `organization_name` | Text | The organization the user belongs to. |
| `created_at`    | Timestamp | User registration timestamp. |

### **Key Features**
- Supabase handles **password hashing and authentication**.
- Each user is associated with **one organization** using `organization_name`.
- Users log in using **email and password**.

---

## **2. Item Table (`items`)**
This table stores **inventory data**, linked to organizations.

| Column Name        | Type      | Description |
|-------------------|----------|-------------|
| `id`             | UUID (PK) | Unique item identifier. |
| `item_name`      | Text      | Name of the inventory item. |
| `quantity`       | Integer   | Current stock level. |
| `location`       | Text      | Storage location of the item. |
| `alert_level`    | Integer   | Minimum stock level before an alert is triggered. |
| `organization_name` | Text | The organization that owns the item. |
| `database_id`    | UUID (FK) | Links the item to a specific inventory database. |
| `created_at`     | Timestamp | Timestamp of item creation. |

### **Key Features**
- Each item is linked to an **organization** using `organization_name`.
- The **alert system** notifies when stock is below `alert_level`.
- **Fast retrieval** via indexing on `organization_name` and `database_id`.

---

## **Future Enhancements: Role-Based Access Control (RLS) and Authorization**
As StockSense expands, **implementing Row-Level Security (RLS) and proper authorization** will further enhance security and scalability:

### **Planned Improvements:**
- **Enforcing RLS** on `items` to ensure users can only access data within their assigned organization.
- **Defining user roles** (e.g., Admin, Viewer) for restricted data modifications.
- **Transitioning from `organization_name` to `organization_id`** for a more structured access model.
- **API authentication improvements** to restrict unauthorized API calls.

These improvements will enable **better access control, secure data isolation, and scalability** as the platform grows.

---

## **Conclusion**
This enhancement **eliminates the local Room database**, transitioning StockSense into a **fully cloud-based inventory system**. Multi-user support, real-time updates, and centralized authentication enhance scalability and accessibility.

---
