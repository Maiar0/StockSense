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

---

## **Conclusion**
This enhancement **eliminates the local Room database**, transitioning StockSense into a **fully cloud-based inventory system**. Multi-user support, real-time updates, and centralized authentication enhance scalability and accessibility.

---
