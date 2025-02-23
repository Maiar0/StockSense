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

## 🔷 Backend Structure

The backend is responsible for handling **authentication, database management, and API interactions** using Supabase as the backend service.

### 🔹 **DataManager** (Data Handling)
📌 **File:** [`DataManager.java`](41)

#### **Functionality:**
- Acts as the **primary data handler** in the app.
- Interfaces with **SupabaseRepository** to fetch, update, insert, and delete items.
- Maintains an **in-memory cache** of items grouped by database.
- Notifies views of data changes.

#### **Key Features:**
✔ Fetches and caches **organization items**.  
✔ Updates **items and organization data**.  
✔ Handles **database creation, import, and deletion**.  
✔ Implements **local storage updates for performance optimization**.

---

### 🔹 **SupabaseRepository** (Supabase API Manager)
📌 **File:** [`SupabaseRepository.java`](42)

#### **Functionality:**
- Handles **authentication, data storage, and API interactions**.
- Stores **authentication tokens** securely in `SharedPreferences`.
- Calls the Supabase API to **fetch, update, and delete items**.

#### **Key Features:**
✔ Manages **user authentication** (Login, Registration, Token Refresh).  
✔ Fetches and updates **organization-related data**.  
✔ Calls **Supabase API endpoints** for database operations.  
✔ Securely **stores authentication tokens**.

---

### 🔹 **SupabaseClient** (Retrofit API Client)
📌 **File:** [`SupabaseClient.java`](43)

#### **Functionality:**
- Provides a **singleton instance of Retrofit** configured with the Supabase API.
- Ensures **consistent API interaction** across the application.

#### **Key Features:**
✔ Centralized **API client configuration**.  
✔ Uses **Gson for JSON serialization**.  
✔ Provides **a single point of API interaction**.

---

### 🔹 **SupabaseApi** (API Endpoints)
📌 **File:** [`SupabaseApi.java`](44)

#### **Functionality:**
- Defines **Retrofit endpoints** for Supabase API interactions.
- Supports **CRUD operations** on the `items` table.
- Handles **authentication, token refresh, and user registration**.

#### **Key Features:**
✔ **Login & Registration** endpoints.  
✔ CRUD endpoints for **items and organization data**.  
✔ Uses `@GET`, `@POST`, `@PATCH`, and `@DELETE` annotations.  
✔ Calls **Supabase RPC functions** for advanced operations.

---

### 🔹 **DataCallback** (Asynchronous Callbacks)
📌 **File:** [`DataCallback.java`](45)

#### **Functionality:**
- Provides a **generic callback interface** for API responses.
- Handles **success and error cases** in API calls.

#### **Key Features:**
✔ Standardized **callback handling**.  
✔ Supports **typed responses** for flexible usage.  
✔ Reduces **boilerplate code** in API interactions.

---

### 🔹 **DatabaseSelection** (Database Model)
📌 **File:** [`DatabaseSelection.java`](46)

#### **Functionality:**
- Represents a **database selection** in the app.
- Stores **database ID and name**.

#### **Key Features:**
✔ Encapsulates **database metadata**.  
✔ Used for **querying and selection**.

---

### 🔹 **Item Model** (Inventory Data Structure)
📌 **File:** [`Item.java`](47)

#### **Functionality:**
- Represents **an item in the database**.
- Stores **item ID, name, quantity, location, and alert level**.
- Maintains **organization and database references**.

#### **Key Features:**
✔ Structured **inventory management**.  
✔ Supports **incremental quantity updates**.  
✔ Implements **basic validation** for safe data operations.

---

### 🔹 **SecureLoginRequest & SecureLoginResponse** (Authentication Models)
📌 **Files:** [`SecureLoginRequest.java`](48), [`SecureLoginResponse.java`](49)

#### **Functionality:**
- Defines **request and response models** for authentication.
- Handles **access tokens, refresh tokens, and user metadata**.

#### **Key Features:**
✔ Secure **login request format**.  
✔ Supports **token-based authentication**.  
✔ Extracts **organization details from metadata**.

---

## 🔷 Backend Workflow Summary

1⃣ **User Authentication →**  
2⃣ **DataManager fetches organization data →**  
3⃣ **User selects a database →**  
4⃣ **Items are fetched, modified, or deleted →**  
5⃣ **Changes are saved to Supabase**

---



