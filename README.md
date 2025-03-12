# 📌 StockSense

## Overview
StockSense is a mobile application designed for **inventory and stock management**. It allows users to manage their organization's databases, track items, and navigate efficiently through various fragments.

This document details the **frontend structure**, explaining each **fragment** and its functionality.

---

## 🔷 Frontend Structure

The frontend now follows a **fragment-based** architecture within a **single activity** (`MainView`). The following fragments are used:

### 🔹 **LoginView (Login Screen)**
📌 **File:** [`LoginView.java`](app/src/main/java/com/CS360/stocksense/LoginView.java)

#### **Functionality:**
- Allows users to **log in** using their email and password.
- Saves credentials using **SharedPreferences**.
- On successful login:
  - Navigates to `JoinOrganizationView` if the user has no assigned organization.
  - Otherwise, navigates to `MainView`.

#### **Key Features:**
✔ Email & Password authentication.  
✔ Stores session tokens securely.  
✔ Redirects users based on organization status.

---

### 🔹 **RegisterUserView (User Registration)**
📌 **File:** [`RegisterUserView.java`](app/src/main/java/com/CS360/stocksense/RegisterUserView.java)

#### **Functionality:**
- Allows new users to register an account.
- Requires email, password, and confirmation.
- Upon successful registration:
  - If no organization is assigned, the user is taken to `JoinOrganizationView`.

#### **Key Features:**
✔ Password validation.  
✔ Secure registration using **Supabase authentication**.  
✔ Redirects to organization selection if needed.

---

### 🔹 **JoinOrganizationView (Organization Management)**
📌 **File:** [`JoinOrganizationView.java`](app/src/main/java/com/CS360/stocksense/JoinOrganizationView.java)

#### **Functionality:**
- Allows users to **join an existing organization** by entering a UUID.
- Provides an option to create a new organization.
- Logs out the user after joining an organization to refresh session data.

#### **Key Features:**
✔ Organization-based user management.  
✔ Updates organization details securely.  
✔ Logs out after joining an organization.

---

### 🔹 **MainView (Primary Activity)**
📌 **File:** [`MainView.java`](app/src/main/java/com/CS360/stocksense/MainView.java)

#### **Functionality:**
- Manages **navigation drawer** for fragment switching.
- Handles **database and item selection**.
- Provides **data export functionality**.
- Supports fragment switching dynamically.

#### **Key Features:**
✔ Central hub for navigation.  
✔ Uses **fragments** for modular design.  
✔ Handles **session management and logout**.  
✔ Manages database and inventory operations.  

---

### 🔹 **DatabaseSelectionFragment (Database Management)**
📌 **File:** [`DatabaseSelectionFragment.java`](app/src/main/java/com/CS360/stocksense/fragments/DatabaseSelectionFragment.java)

#### **Functionality:**
- Displays a **list of available databases** in a **RecyclerView**.
- Users can:
  - **Create** a new database.
  - **Delete** a database by ID.
  - **Import** a database using a CSV file.
  - **Select** a database to navigate to `SearchFragment`.

#### **Key Features:**
✔ Database creation & deletion.  
✔ CSV import functionality.  
✔ Seamless navigation to search and manage items.  

---

### 🔹 **SearchFragment (Item Search)**
📌 **File:** [`SearchFragment.java`](app/src/main/java/com/CS360/stocksense/fragments/SearchFragment.java)

#### **Functionality:**
- Provides a **search interface** to browse and manage items within a database.
- Displays search results in a **RecyclerView**.
- Users can:
  - **Search for items** by name or ID.
  - **Navigate to item details** (`ItemDetailsFragment`).
  - **Delete items**.
  - **Export data** to a CSV file.

#### **Key Features:**
✔ Efficient **search functionality** with filters.  
✔ CSV export capability.  
✔ Direct navigation to item details.

---

### 🔹 **GridFragment (Grid View for Items)**
📌 **File:** [`GridFragment.java`](app/src/main/java/com/CS360/stocksense/fragments/GridFragment.java)

#### **Functionality:**
- Displays items in a **grid format** using a **RecyclerView**.
- Users can:
  - View inventory in a **2-column grid layout**.
  - **Create new items**.
  - **Navigate to `ItemDetailsFragment`** for item management.
  - **Export** the database.
  - **Increase or decrease item quantity** using `+` and `-` buttons.

#### **Key Features:**
✔ Grid-based inventory display.  
✔ Item creation workflow.  
✔ CSV export for data backup.  
✔ Direct in-grid inventory updates using `+` and `-` buttons.

---

### 🔹 **ItemDetailsFragment (Item Management)**
📌 **File:** [`ItemDetailsFragment.java`](app/src/main/java/com/CS360/stocksense/fragments/ItemDetailsFragment.java)

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

### 🔹 **SettingsFragment (User Preferences)**
📌 **File:** [`SettingsFragment.java`](app/src/main/java/com/CS360/stocksense/fragments/SettingsFragment.java)

#### **Functionality:**
- Allows users to change **application settings**.
- Supports **changing organizations** from the settings menu.
- Logs users out for changes to take effect.

#### **Key Features:**
✔ Preference-based user settings.  
✔ Organization switching functionality.  
✔ Integrated with `JoinOrganizationView`.

---

## 🔷 Navigation & Workflow Summary

1️⃣ **Login/Register →**  
2️⃣ **JoinOrganization →**  
3️⃣ **MainView (Fragment Navigation) →**  
4️⃣ **DatabaseSelectionFragment →**  
5️⃣ **SearchFragment/GridFragment (Browse Inventory) →**  
6️⃣ **ItemDetailsFragment (Manage Specific Item)**


---

## 🔷 Backend Structure

The backend is responsible for handling **authentication, database management, and API interactions** using Supabase as the backend service.

### 🔹 **Database Schema & Relations**

The StockSense backend utilizes **Supabase PostgreSQL** with the following key tables:

#### **items** (Inventory Table)
- `id` (int4) - Primary key
- `item_name` (varchar) - Name of the item
- `quantity` (int4) - Current stock level
- `location` (varchar) - Storage location
- `alert_level` (int4) - Quantity threshold for alerts
- `database_id` (bpchar) - Associated database
- `database_name` (varchar) - Name of the database
- `item_id` (varchar) - Unique identifier for item
- `organization_id` (uuid) - Foreign key referencing `organizations.id`

#### **organizations** (Company/Group Management)
- `id` (uuid) - Primary key
- `name` (text) - Organization name
- `created_at` (timestamp) - Timestamp of creation

#### **auth.users** (Supabase Authentication Table)
- `id` (uuid) - User ID (Supabase-managed authentication)
- `email` (text) - User's email
- `organization_id` (uuid) - Foreign key linking user to `organizations`
- `created_at` (timestamp) - User creation timestamp

---

### 🔹 **DataManager** (Data Handling)
📌 **File:** [`DataManager.java`](app/src/main/java/com/CS360/stocksense/database/DataManager.java)

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
📌 **File:** [`SupabaseRepository.java`](app/src/main/java/com/CS360/stocksense/auth/SupabaseRepository.java)

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

### 🔹 **SupabaseApi** (API Endpoints & RPC Calls)
📌 **File:** [`SupabaseApi.java`](app/src/main/java/com/CS360/stocksense/auth/SupabaseApi.java)

#### **Functionality:**
- Defines **Retrofit endpoints** for Supabase API interactions.
- Supports **CRUD operations** on the `items` table.
- Handles **authentication, token refresh, and user registration**.
- Implements **RPC endpoints for advanced functionality**.

#### **Key RPC Functions:**
✔ `update_item_quantity` - **Modifies an item's quantity** dynamically.  
✔ `update_organization_id` - **Assigns a user to an organization**.  
✔ `create_organization` - **Creates a new organization** and assigns a user.  

#### **Other API Endpoints:**
✔ **Login & Registration** endpoints.  
✔ CRUD endpoints for **items and organization data**.  
✔ Uses `@GET`, `@POST`, `@PATCH`, and `@DELETE` annotations.  
✔ Calls **Supabase RPC functions** for advanced operations.

---

### 🔹 **User Authentication & Organization Management**

StockSense leverages **Supabase Auth** for managing users:

1. **Login & Registration:**
   - Users sign up with **email and password**.
   - `organization_id` is linked to the user metadata.

2. **Organization Assignment:**
   - When a user registers, they are assigned a default `organization_id`.
   - Admins can reassign users using `update_organization_id`.

3. **Permissions & Security:**
   - Users can only fetch items linked to their **organization_id**.
   - Role-based access control (RBAC) is enforced through **Supabase RLS policies**.

---

## 🔷 Backend Workflow Summary

1⃣ **User Authentication →**  
2⃣ **DataManager fetches organization data →**  
3⃣ **User selects a database →**  
4⃣ **Items are fetched, modified, or deleted →**  
5⃣ **Changes are saved to Supabase**

---

## 📌 TODOs Before Completion

1️⃣  **Client-side Sensitive Data:**
   - Eliminate debug logs printing API keys, access tokens, or sensitive data.
   - Clean up logs in `SupabaseRepository.java` and `SupabaseApi.java`.
   - Ensure sensitive data is stored appropriatley. 


---

## 🔒 Security Considerations

Ensuring data security and protecting user information is a priority in StockSense. Below are the key security measures and best practices implemented:

### 1️⃣ **Authentication & Authorization**
- Supabase **JWT-based authentication** ensures secure user sessions.
- **Role-Based Access Control (RBAC)** is enforced through **Row-Level Security (RLS) policies**.
- Users can only access **items linked to their `organization_id`**.

### 2️⃣ **Data Protection & Encryption**
- **Passwords** are securely hashed using Supabase authentication mechanisms.
- **API keys and tokens** are stored securely and never exposed in logs.
- **All network communication** is encrypted using **HTTPS**.

### 3️⃣ **Secure API Access**
- **API keys** are stored securely in environment variables.
- **Supabase RPC functions** handle critical actions to prevent direct database manipulation.
- Only **authenticated requests** can modify database records.

### 4️⃣ **Logging & Monitoring**
- Debug logs **do not print sensitive data** such as API keys or access tokens.
- **Error handling** ensures that internal errors do not expose sensitive system details.
- **Audit logs** track authentication and key API interactions for security oversight.

### 5️⃣ **Preventing SQL Injection & Data Tampering**
- **Parameterized queries** are used for all database interactions.
- Supabase **RLS policies** prevent unauthorized data modifications.
- **Input validation** is enforced to ensure safe and expected values are processed.

### ✅ **Next Steps in Security**
- Regularly review **Supabase security updates and patches**.
- Implement **multi-factor authentication (MFA)** for enhanced security.
- Enforce **strong password policies** for all user accounts.

---

## 🚀 Future Development Items

To enhance StockSense further, the following features are planned for future development:

### 1️⃣ **User Experience Enhancements**
- **Light Mode / Dark Mode**: Implementing a theme switcher for better accessibility.
- **User Settings Panel**: Allowing users to customize notifications, themes, and preferences.

### 2️⃣ **Admin Console**
- **MEVN Stack Admin Console**: Developing an admin dashboard using **MongoDB, Express, Vue.js, and Node.js**.
- **Improved Organization Management**: Enhancing role-based access with fine-grained permissions.

### 3️⃣ **Expanded Organizational Role Structure**
- Introduce **more granular roles** beyond standard users and admins.
- Support for **custom role creation** and **permission delegation**.
