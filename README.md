# Enhancement One: User Interface Workflow and Views

This enhancement introduces **improved UI navigation and workflow** within the StockSense inventory management application. It focuses on **database selection, inventory browsing, item management, and search functionalities**, ensuring seamless interaction between different views.

---

## **Overview of the UI Flow**

1. **Login View (`LoginViewActivity`)**  
   - Allows users to **enter their organization name** to log in.
   - Validates network connection and organization credentials.
   - Stores Organization Name using `SharedPreferences` for session persistence.
   - **Next Step:** Redirects users to `DbSelectionViewActivity`.

2. **Database Selection View (`DbSelectionViewActivity`)**  
   - Displays available databases in a **RecyclerView list**.
   - Users can:
     - **Create** a new database.
     - **Delete** an existing database by entering its ID.
     - **Import** a database from a CSV file.
   - **Next Step:** Selecting a database navigates to `GridViewActivity`.

3. **Grid View (`GridViewActivity`)**  
   - Displays items from the selected database in a **grid format** using a `RecyclerView`.
   - Users can:
     - **Add new items** with a structured input dialog.
     - **Export database** contents to a CSV file.
   - **Next Step:** Clicking an item navigates to `ItemDetailsActivity`.

4. **Item Details View (`ItemDetailsActivity`)**  
   - Displays **detailed information** for an individual inventory item.
   - Users can:
     - **Modify item attributes** (name, quantity, location, alert level).
     - **Delete** an item with confirmation.
   - **Next Step:** Saves changes and returns to `GridViewActivity`.

5. **Search View (`SearchViewActivity`)**  
   - Provides a **search bar** to filter inventory items by ID or name.
   - Uses **hash maps** for fast lookups.
   - Displays results dynamically in a `RecyclerView` list.
   - **Next Step:** Clicking an item navigates to `ItemDetailsActivity`.

6. **Table View (`TableViewActivity`)** *(Currently Unused)*
   - Originally intended for displaying inventory in a **table-based format**.
   - Future consideration for removal.

---

## **Detailed View Descriptions**

### **1. Login View**
> **File:** [`LoginViewActivity.java`](app/src/main/java/com/CS360/stocksense/LoginViewActivity.java)

#### **Features**
- Accepts **organization name** for login.
- Validates **network connectivity** before proceeding.
- Stores login information using `SharedPreferences`.

#### **Navigation**
- **On Successful Login:** Redirects to `DbSelectionViewActivity`.

---

### **2. Database Selection View**
> **File:** [`DbSelectionViewActivity.java`](app/src/main/java/com/CS360/stocksense/DbSelectionViewActivity.java)

#### **Features**
- Displays **list of available databases** using `RecyclerView`.
- Supports:
  - **Creating new databases** with a user-inputted name.
  - **Deleting databases** via unique ID.
  - **Importing databases** from CSV files.

#### **Navigation**
- **On Database Selection:** Opens `GridViewActivity` with the selected database.

---

### **3. Grid View**
> **File:** [`GridViewActivity.java`](app/src/main/java/com/CS360/stocksense/GridViewActivity.java)

#### **Features**
- Displays **inventory items in a grid** using a `RecyclerView`.
- Allows users to:
  - **Add new inventory items**.
  - **Export the database** as a CSV file.

#### **Navigation**
- **On Item Click:** Opens `ItemDetailsActivity` for item modification.

---

### **4. Item Details View**
> **File:** [`ItemDetailsActivity.java`](app/src/main/java/com/CS360/stocksense/ItemDetailsActivity.java)

#### **Features**
- Shows detailed **item information**.
- Allows:
  - **Editing item attributes** (name, quantity, location, alert level).
  - **Deleting the item** with confirmation.

#### **Navigation**
- **After Saving Changes:** Returns to `GridViewActivity`.

---

### **5. Search View**
> **File:** [`SearchViewActivity.java`](app/src/main/java/com/CS360/stocksense/SearchViewActivity.java)

#### **Features**
- Implements a **search bar** for real-time filtering.
- Uses **dual hash maps** for **fast lookups** by:
  - **Item ID**
  - **Item Name**
- Displays search results in a `RecyclerView`.

#### **Navigation**
- **On Item Click:** Opens `ItemDetailsActivity`.

---

### **6. Table View (Deprecated)**
> **File:** [`TableViewActivity.java`](app/src/main/java/com/CS360/stocksense/TableViewActivity.java)

#### **Status**
- **Currently unused.**
- Originally designed to display inventory in a **table format**.
- Considered for removal in future versions.

---

## **Conclusion**
This enhancement **streamlines UI interactions** by ensuring a logical **workflow** between views. Users can **easily navigate** between database selection, inventory management, and item searches, improving usability and efficiency.

---
