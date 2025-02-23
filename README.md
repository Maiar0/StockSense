## 📌 StockSense – README

### Overview
StockSense is a mobile application designed for inventory and stock management. It allows users to manage their organization's databases, track items, and navigate through various views efficiently.

This document details the **frontend structure**, explaining each **activity (view)**, its workflow, and key features.

---

## 🔷 Frontend Structure

The frontend consists of several key **activities (views)**, each responsible for handling specific user interactions.

---

### 🔹 **LoginActivity** (Login Screen)
📌 **File:** [`LoginActivity.java`](18)

#### **Functionality:**
- Allows users to **log in** using their email and password.
- Saves credentials using **SharedPreferences**.
- On successful login:
  - Navigates to `JoinOrganization` if the user has no assigned organization.
  - Otherwise, navigates to `DbSelectionViewActivity`.

#### **Key Features:**
✔ Email & Password authentication.  
✔ Stores session tokens securely.  
✔ Redirects users based on organization status.

---

### 🔹 **RegisterUserActivity** (User Registration)
📌 **File:** [`RegisterUserActivity.java`](20)

#### **Functionality:**
- Allows new users to register an account.
- Requires email, password, and confirmation.
- Upon successful registration:
  - If no organization is assigned, the user is taken to `JoinOrganization`.

#### **Key Features:**
✔ Password validation.  
✔ Secure registration using **Supabase authentication**.  
✔ Redirects to organization selection if needed.

---

### 🔹 **JoinOrganization** (Organization Management)
📌 **File:** [`JoinOrganization.java`](17)

#### **Functionality:**
- Allows users to **join an existing organization** by entering a UUID.
- Provides an option to create a new organization (pending implementation).
- Logs out the user after joining an organization to refresh session data.

#### **Key Features:**
✔ Organization-based user management.  
✔ Updates organization details securely.  
✔ Logs out after joining an organization.

---

### 🔹 **DbSelectionViewActivity** (Database Selection)
📌 **File:** [`DbSelectionViewActivity.java`](14)

#### **Functionality:**
- Displays a **list of available databases** in a **RecyclerView**.
- Users can:
  - **Create** a new database.
  - **Delete** a database by ID.
  - **Import** a database using a CSV file.
  - **Select** a database to navigate to `SearchViewActivity`.

#### **Key Features:**
✔ Database creation & deletion.  
✔ CSV import functionality.  
✔ Seamless navigation to search and manage items.  

---

### 🔹 **SearchViewActivity** (Item Search)
📌 **File:** [`SearchViewActivity.java`](21)

#### **Functionality:**
- Provides a **search interface** to browse and manage items within a database.
- Displays search results in a **RecyclerView**.
- Users can:
  - **Search for items** by name or ID.
  - **Navigate to item details** (`ItemDetailsActivity`).
  - **Delete items**.
  - **Export data** to a CSV file.

#### **Key Features:**
✔ Efficient **search functionality** with filters.  
✔ CSV export capability.  
✔ Direct navigation to item details.

---

### 🔹 **GridViewActivity** (Grid View for Items)
📌 **File:** [`GridViewActivity.java`](15)

#### **Functionality:**
- Displays items in a **grid format** using a **RecyclerView**.
- Users can:
  - View inventory in a **2-column grid layout**.
  - **Create new items**.
  - **Navigate to `ItemDetailsActivity`** for item management.
  - **Export** the database.
  - **Increase or decrease item quantity** using `+` and `-` buttons.

#### **Key Features:**
✔ Grid-based inventory display.  
✔ Item creation workflow.  
✔ CSV export for data backup.  
✔ Direct in-grid inventory updates using `+` and `-` buttons.

---

### 🔹 **ItemDetailsActivity** (Item Management)
📌 **File:** [`ItemDetailsActivity.java`](16)

#### **Functionality:**
- Displays **detailed information** about a selected item.
- Users can:
  - **Edit** item attributes (name, quantity, location, alert level).
  - **Delete** an item with confirmation.

#### **Key Features:**
✔ Full item editing functionality.  
✔ Secure update operations.  
✔ Confirmation prompts for critical actions.

---

## 🔷 Navigation & Workflow Summary

1⃣ **Login/Register →**  
2⃣ **JoinOrganization →**  
3⃣ **DbSelectionViewActivity (Database Selection) →**  
4⃣ **SearchViewActivity / GridViewActivity (Browse Inventory) →**  
5⃣ **ItemDetailsActivity (Manage Specific Item)**

---


