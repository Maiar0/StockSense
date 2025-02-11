# Enhancement Two: SearchView Optimization

This enhancement focuses on **improving the SearchView functionality** within StockSense, ensuring a **faster, more intuitive** search experience for users.

---

## **Overview of Search Enhancements**

1. **Optimized Search Algorithm**
   - Implemented **hash map indexing** for faster lookups.
   - Reduces **iteration overhead** by maintaining separate maps for:
     - **Item ID lookups**
     - **Item Name lookups** (case-insensitive)
   
2. **Real-Time Filtering**
   - Dynamically updates the **RecyclerView** as users type.
   - Implements a **300ms debounce delay** to prevent excessive searches.
   
3. **Improved User Experience**
   - Displays **instant feedback** (e.g., "No Results Found" message).
   - Allows **partial and case-insensitive searches**.

---

## **Technical Changes in `SearchViewActivity`**

### **1. Hash Map Indexing for Fast Lookups**
> **File:** [`SearchViewActivity.java`](app/src/main/java/com/CS360/stocksense/SearchViewActivity.java)

- Created **two HashMaps**:
  ```java
  private Map<String, Item> itemIdMap = new HashMap<>();
  private Map<String, Item> itemNameMap = new HashMap<>();
  ```
- Populated during data fetch:
  ```java
  private void initializeHashMaps(){
      for (Item item : fetchedItems) {
          itemIdMap.put(item.getItem_id(), item);
          itemNameMap.put(item.getItemName().toLowerCase(), item);
      }
  }
  ```
- Allows **instant lookup** without iterating over the full list.

### **2. Real-Time Filtering with Debounce**
- Uses `TextWatcher` and a `Handler` for delayed execution:
  ```java
  searchBox.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
          new Handler().postDelayed(() -> filterItems(s.toString().trim()), 300);
      }
  });
  ```
- Prevents unnecessary filtering **until typing stops**.

### **3. Case-Insensitive Search with Partial Matching**
- Updated filtering logic:
  ```java
  private void filterItems(String query) {
      List<Item> filteredList = new ArrayList<>();
      if (query.isEmpty()) {
          filteredList.addAll(fetchedItems);
      } else {
          if (itemIdMap.containsKey(query)) {
              filteredList.add(itemIdMap.get(query));
          } else {
              for (String key : itemNameMap.keySet()) {
                  if (key.contains(query.toLowerCase())) {
                      filteredList.add(itemNameMap.get(key));
                  }
              }
          }
      }
      updateRecyclerView(filteredList);
  }
  ```

---

## **Conclusion**
These enhancements **significantly improve** search speed and usability. Users now experience **faster results, better filtering, and improved accuracy** when searching for inventory items.

---
